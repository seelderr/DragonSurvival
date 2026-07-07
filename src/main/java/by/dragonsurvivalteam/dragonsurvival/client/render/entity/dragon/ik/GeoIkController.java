package by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.ik;

import au.edu.federation.caliko.FabrikBone3D;
import au.edu.federation.caliko.FabrikChain3D;
import au.edu.federation.utils.Vec3f;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.animation.state.BoneSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class GeoIkController<T extends GeoAnimatable> {

    @Nullable
    private Vector3d targetLocalPosition;
    private double groundRayLength;
    private boolean matricesReady;
    @Nullable
    private Vec3 debugPreIkBonePosition;
    @Nullable
    private Vec3 debugTargetPosition;
    private static final float MIN_SEGMENT_LENGTH = 0.0001f;
    private static final double DEBUG_SPHERE_RADIUS = 0.25;
    private static final int DEBUG_SPHERE_SEGMENTS = 16;

    private final List<GeoIkChainDefinition> chains = new ArrayList<>();
    private final Map<GeoBone, Vector3f> preIkRotations = new IdentityHashMap<>();

    public void applyIk(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, MultiBufferSource bufferSource, float partialTick) {
        prepare(bakedModel);
        Vector3d target = this.groundRayLength > 0 && this.matricesReady
                ? findGroundTarget(animatable, bakedModel, partialTick)
                : this.targetLocalPosition;
        if (this.groundRayLength > 0 && target == null) {
            // The animation system has already populated the bone rotations for this frame.
            // A missing ground correction must leave that pose untouched.
            this.preIkRotations.clear();
        } else {
            savePreIkRotations(bakedModel);
            apply(bakedModel, target);
        }
        renderDebugPositions(poseStack, bufferSource);
        this.matricesReady = true;
    }

    private void savePreIkRotations(BakedGeoModel model) {
        this.preIkRotations.clear();

        for (GeoIkChainDefinition definition : this.chains) {
            GeoBone anchor = model.getBone(definition.anchorBoneName()).orElse(null);
            GeoBone root = model.getBone(definition.controlledRootBoneName()).orElse(null);

            if (anchor == null || root == null) {
                continue;
            }

            for (GeoBone bone : pathFromRootToEffector(root, anchor)) {
                this.preIkRotations.put(
                        bone,
                        new Vector3f(bone.getRotX(), bone.getRotY(), bone.getRotZ())
                );
            }
        }
    }

    public void restorePreIkRotations() {
        this.preIkRotations.forEach((bone, rotation) ->
                bone.updateRotation(rotation.x, rotation.y, rotation.z)
        );
        this.preIkRotations.clear();
    }

    public GeoIkController<T> targetGround(double rayLength) {
        if (rayLength <= 0) {
            throw new IllegalArgumentException("Ground ray length must be positive");
        }

        this.groundRayLength = rayLength;
        this.targetLocalPosition = null;

        return this;
    }

    public GeoIkController<T> addChain(String anchorBoneName, String controlledRootBoneName) {
        return addChain(new GeoIkChainDefinition(anchorBoneName, controlledRootBoneName));
    }

    public GeoIkController<T> addChain(GeoIkChainDefinition chain) {
        this.chains.add(chain);

        return this;
    }

    public List<GeoIkChainDefinition> chains() {
        return List.copyOf(this.chains);
    }

    public Set<GeoBone> matrixUpdatePath(BakedGeoModel model) {
        Set<GeoBone> updatePath = Collections.newSetFromMap(new IdentityHashMap<>());

        for (GeoIkChainDefinition definition : this.chains) {
            GeoBone anchor = model.getBone(definition.anchorBoneName()).orElse(null);
            GeoBone root = model.getBone(definition.controlledRootBoneName()).orElse(null);

            if (anchor == null || root == null) {
                continue;
            }

            List<GeoBone> chain = pathFromRootToEffector(root, anchor);
            if (chain.isEmpty()) {
                continue;
            }

            for (GeoBone bone : chain) {
                bone.setTrackingMatrices(true);
                addBoneAndAncestors(updatePath, bone);
            }

            addBoneAndDescendants(updatePath, anchor);

            if (root.getParent() != null) {
                root.getParent().setTrackingMatrices(true);
                addBoneAndAncestors(updatePath, root.getParent());
            }
        }

        return updatePath;
    }

    private static void addBoneAndAncestors(Set<GeoBone> bones, GeoBone bone) {
        for (GeoBone current = bone; current != null; current = current.getParent()) {
            bones.add(current);
        }
    }

    private static void addBoneAndDescendants(Set<GeoBone> bones, GeoBone bone) {
        bone.setTrackingMatrices(true);
        bones.add(bone);

        for (GeoBone child : bone.getChildBones()) {
            addBoneAndDescendants(bones, child);
        }
    }

    public void prepare(BakedGeoModel model) {
        for (GeoIkChainDefinition definition : this.chains) {
            Optional<GeoBone> anchor = model.getBone(definition.anchorBoneName());
            Optional<GeoBone> root = model.getBone(definition.controlledRootBoneName());

            if (anchor.isEmpty() || root.isEmpty()) {
                continue;
            }

            anchor.get().setTrackingMatrices(true);
            if (root.get().getParent() != null) {
                root.get().getParent().setTrackingMatrices(true);
            }

            List<GeoBone> path = pathFromRootToEffector(root.get(), anchor.get());
            for (GeoBone bone : path) {
                bone.setTrackingMatrices(true);
            }
        }
    }

    private void apply(BakedGeoModel model) {
        apply(model, null);
    }

    private void apply(BakedGeoModel model, @Nullable Vector3d targetLocalPosition) {
        for (GeoIkChainDefinition definition : this.chains) {
            Optional<GeoBone> anchor = model.getBone(definition.anchorBoneName());
            Optional<GeoBone> root = model.getBone(definition.controlledRootBoneName());

            if (anchor.isEmpty() || root.isEmpty()) {
                continue;
            }

            Vector3d target = targetLocalPosition == null ? anchor.get().getModelPosition() : targetLocalPosition;
            apply(root.get(), anchor.get(), target);
        }
    }

    private void apply(GeoBone root, GeoBone anchor, Vector3d targetLocalPosition) {
        List<GeoBone> path = pathFromRootToEffector(root, anchor);

        if (path.size() < 2) {
            return;
        }

        FabrikChain3D chain = buildChain(path);

        if (chain.getNumBones() == 0) {
            return;
        }

        Vec3f target = toVec3f(targetLocalPosition);
        chain.solveForTarget(target);
        applySolvedRotations(path, chain);
    }

    private FabrikChain3D buildChain(List<GeoBone> path) {
        FabrikChain3D chain = new FabrikChain3D();

        for (int i = 0; i < path.size() - 1; i++) {
            Vec3f start = toVec3f(path.get(i).getModelPosition());
            Vec3f end = toVec3f(path.get(i + 1).getModelPosition());

            if (Vec3f.distanceBetween(start, end) <= MIN_SEGMENT_LENGTH) {
                continue;
            }

            FabrikBone3D bone = new FabrikBone3D(start, end, path.get(i).getName());

            if (chain.getNumBones() == 0) {
                chain.addBone(bone);
            } else {
                chain.addConsecutiveBone(bone);
            }
        }

        return chain;
    }

    private void applySolvedRotations(List<GeoBone> path, FabrikChain3D chain) {
        int solvedBoneIndex = 0;
        Quaternionf previousDesiredModelRotation = null;
        GeoBone anchor = path.getLast();
        Quaternionf anchorModelRotation = modelRotation(anchor);

        for (int i = 0; i < path.size() - 1; i++) {
            GeoBone bone = path.get(i);
            Vector3f currentDirection = directionBetween(path.get(i).getModelPosition(), path.get(i + 1).getModelPosition());

            if (currentDirection.lengthSquared() <= MIN_SEGMENT_LENGTH) {
                if (previousDesiredModelRotation != null
                        && i > 0
                        && bone.getParent() == path.get(i - 1)) {
                    previousDesiredModelRotation.mul(localRotation(bone));
                }

                continue;
            }

            if (solvedBoneIndex >= chain.getNumBones()) {
                break;
            }

            FabrikBone3D solvedBone = chain.getBone(solvedBoneIndex++);
            Vector3f solvedDirection = directionBetween(solvedBone.getStartLocation(), solvedBone.getEndLocation());

            if (solvedDirection.lengthSquared() <= MIN_SEGMENT_LENGTH) {
                continue;
            }

            currentDirection.normalize();
            solvedDirection.normalize();

            // GeoBone#getModelPosition mirrors X relative to the matrices used by the renderer.
            currentDirection.x = -currentDirection.x;
            solvedDirection.x = -solvedDirection.x;

            Quaternionf modelCorrection = new Quaternionf().rotationTo(currentDirection, solvedDirection);
            Quaternionf currentModelRotation = modelRotation(bone);
            Quaternionf desiredModelRotation = modelCorrection.mul(currentModelRotation, new Quaternionf());
            Quaternionf parentModelRotation;
            if (i > 0 && bone.getParent() == path.get(i - 1) && previousDesiredModelRotation != null) {
                parentModelRotation = previousDesiredModelRotation;
            } else {
                parentModelRotation = bone.getParent() == null
                        ? new Quaternionf()
                        : modelRotation(bone.getParent());
            }
            Quaternionf desiredLocalRotation = parentModelRotation
                    .invert(new Quaternionf())
                    .mul(desiredModelRotation);
            Vector3f rotation = desiredLocalRotation.getEulerAnglesZYX(new Vector3f());

            bone.updateRotation(rotation.x, rotation.y, rotation.z);
            previousDesiredModelRotation = desiredModelRotation;
        }

        if (previousDesiredModelRotation != null && anchor.getParent() == path.get(path.size() - 2)) {
            Quaternionf anchorLocalRotation = previousDesiredModelRotation
                    .invert(new Quaternionf())
                    .mul(anchorModelRotation)
                    .normalize();
            Vector3f rotation = anchorLocalRotation.getEulerAnglesZYX(new Vector3f());

            anchor.updateRotation(rotation.x, rotation.y, rotation.z);
        }
    }

    private static Quaternionf localRotation(GeoBone bone) {
        return new Quaternionf().rotationZYX(bone.getRotZ(), bone.getRotY(), bone.getRotX());
    }

    private static Quaternionf modelRotation(GeoBone bone) {
        // Scale can be present in the tracked matrix, so normalize each basis vector first.
        Matrix3f rotation = bone.getModelSpaceMatrix().normalize3x3(new Matrix3f());

        return new Quaternionf().setFromNormalized(rotation);
    }

    private static List<GeoBone> pathFromRootToEffector(GeoBone root, GeoBone anchor) {
        List<GeoBone> path = new ArrayList<>();

        if (!collectPathToAnchor(root, anchor, path)) {
            path.clear();
        }

        return path;
    }

    private static boolean collectPathToAnchor(GeoBone bone, GeoBone anchor, List<GeoBone> path) {
        path.add(bone);

        if (bone == anchor) {
            return true;
        }

        for (GeoBone child : bone.getChildBones()) {
            if (collectPathToAnchor(child, anchor, path)) {
                return true;
            }
        }

        path.removeLast();

        return false;
    }

    private static Vec3f toVec3f(org.joml.Vector3d vector) {
        return new Vec3f((float) vector.x, (float) vector.y, (float) vector.z);
    }

    private static Vector3f directionBetween(org.joml.Vector3d start, org.joml.Vector3d end) {
        return new Vector3f(
                (float) (end.x - start.x),
                (float) (end.y - start.y),
                (float) (end.z - start.z)
        );
    }

    private static Vector3f directionBetween(Vec3f start, Vec3f end) {
        return new Vector3f(end.x - start.x, end.y - start.y, end.z - start.z);
    }

    @Nullable
    private Vector3d findGroundTarget(T animatable, BakedGeoModel bakedModel, float partialTick) {
        if (!(animatable instanceof Entity entity) || chains().isEmpty()) {
            return null;
        }

        var definition = chains().getFirst();
        GeoBone anchor = bakedModel.getBone(definition.anchorBoneName()).orElse(null);

        if (anchor == null) {
            return null;
        }

        Vector3d trackedAnchorPosition = anchor.getWorldPosition();
        Vec3 anchorWorld = new Vec3(trackedAnchorPosition.x, trackedAnchorPosition.y, trackedAnchorPosition.z);

        if (animatable instanceof DragonEntity dragon
                && dragon.getPlayer() != null) {
            anchorWorld = anchorWorld.subtract(DragonRenderer.getModelOffset(dragon, partialTick));
        }

        // This assignment happens before apply(...) and intentionally preserves that position for debug rendering.
        this.debugPreIkBonePosition = anchorWorld;

        return moveTargetOutOfBlocks(entity, anchorWorld, anchor.getModelPosition(), anchor);
    }

    private Vector3d moveTargetOutOfBlocks(Entity entity, Vec3 anchorWorld, Vector3d originModelPosition, GeoBone anchor) {
        BlockHitResult collision = entity.level().clip(new ClipContext(
                anchorWorld.add(0, this.groundRayLength, 0),
                anchorWorld,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                entity
        ));

        if (collision.getType() == HitResult.Type.MISS) {
            this.debugTargetPosition = null;
            return null;
        }

        BlockPos collisionPos = collision.getBlockPos();
        VoxelShape collisionShape = entity.level()
                .getBlockState(collisionPos)
                .getCollisionShape(entity.level(), collisionPos);
        double collisionTop = collisionPos.getY() + collisionShape.max(Direction.Axis.Y);
        double soleClearance = geometryClearanceBelowAnchor(anchor);
        double targetY = collisionTop + soleClearance;

        // Ground IK only corrects penetration. Pulling a raised foot down would replace
        // the vertical component of walk/run animations instead of preserving it.
        if (targetY <= anchorWorld.y) {
            this.debugTargetPosition = null;
            return null;
        }

        Vec3 targetWorld = new Vec3(anchorWorld.x, targetY, anchorWorld.z);
        this.debugTargetPosition = targetWorld;

        Matrix4f modelToWorld = new Matrix4f(anchor.getWorldSpaceMatrix())
                .mul(new Matrix4f(anchor.getModelSpaceMatrix()).invert());
        Vector3f modelDelta = modelToWorld.invert().transformDirection(new Vector3f(
                (float) (targetWorld.x - anchorWorld.x),
                (float) (targetWorld.y - anchorWorld.y),
                (float) (targetWorld.z - anchorWorld.z)
        ));

        // GeckoLib stores vectors with an inverted X and divided by 16 when in model space, so we need to follow that behavior
        return new Vector3d(
                originModelPosition.x - modelDelta.x * 16,
                originModelPosition.y + modelDelta.y * 16,
                originModelPosition.z + modelDelta.z * 16
        );
    }

    private static double geometryClearanceBelowAnchor(GeoBone anchor) {
        Vector3f anchorPosition = anchor.getWorldSpaceMatrix().transformPosition(new Vector3f());
        double lowestGeometryY = lowestGeometryY(anchor, Double.POSITIVE_INFINITY);

        if (!Double.isFinite(lowestGeometryY)) {
            return 0;
        }

        return java.lang.Math.max(0, anchorPosition.y - lowestGeometryY);
    }

    private static double lowestGeometryY(GeoBone bone, double lowestY) {
        if (!bone.isHidden()) {
            Matrix4f boneTransform = new Matrix4f(bone.getWorldSpaceMatrix())
                    .translate(
                            -bone.getPivotX() / 16f,
                            -bone.getPivotY() / 16f,
                            -bone.getPivotZ() / 16f
                    );

            for (GeoCube cube : bone.getCubes()) {
                Matrix4f cubeTransform = new Matrix4f(boneTransform)
                        .translate(
                                (float)cube.pivot().x() / 16f,
                                (float)cube.pivot().y() / 16f,
                                (float)cube.pivot().z() / 16f
                        )
                        .rotateZ((float)cube.rotation().z())
                        .rotateY((float)cube.rotation().y())
                        .rotateX((float)cube.rotation().x())
                        .translate(
                                -(float)cube.pivot().x() / 16f,
                                -(float)cube.pivot().y() / 16f,
                                -(float)cube.pivot().z() / 16f
                        );

                for (var quad : cube.quads()) {
                    if (quad == null) {
                        continue;
                    }

                    for (var vertex : quad.vertices()) {
                        float vertexY = cubeTransform.transformPosition(
                                new Vector3f(vertex.position())
                        ).y;
                        lowestY = java.lang.Math.min(lowestY, vertexY);
                    }
                }
            }
        }

        if (!bone.isHidingChildren()) {
            for (GeoBone child : bone.getChildBones()) {
                lowestY = lowestGeometryY(child, lowestY);
            }
        }

        return lowestY;
    }

    private void renderDebugPositions(PoseStack poseStack, MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();

        if (!minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            return;
        }

        Vec3 camera = minecraft.gameRenderer.getMainCamera().getPosition();
        Matrix4f inversePose = new Matrix4f(poseStack.last().pose()).invert();
        VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());

        // Red is the tracked bone position captured before IK; green is the collision-adjusted target.
        if (this.debugPreIkBonePosition != null) {
            renderDebugSphere(poseStack, lines, camera, inversePose, this.debugPreIkBonePosition, 255, 64, 64);
        }

        if (this.debugTargetPosition != null) {
            renderDebugSphere(poseStack, lines, camera, inversePose, this.debugTargetPosition, 64, 255, 64);
        }
    }

    private static void renderDebugSphere(PoseStack poseStack, VertexConsumer lines, Vec3 camera,
                                          Matrix4f inversePose, Vec3 center, int red, int green, int blue) {
        for (int plane = 0; plane < 3; plane++) {
            for (int segment = 0; segment < DEBUG_SPHERE_SEGMENTS; segment++) {
                double startAngle = java.lang.Math.PI * 2 * segment / DEBUG_SPHERE_SEGMENTS;
                double endAngle = java.lang.Math.PI * 2 * (segment + 1) / DEBUG_SPHERE_SEGMENTS;
                Vector3f start = toPosePosition(debugCirclePoint(center, plane, startAngle), camera, inversePose);
                Vector3f end = toPosePosition(debugCirclePoint(center, plane, endAngle), camera, inversePose);
                Vector3f direction = new Vector3f(end).sub(start).normalize();
                PoseStack.Pose pose = poseStack.last();

                lines.addVertex(pose, start).setColor(red, green, blue, 255)
                        .setNormal(pose, direction.x, direction.y, direction.z);
                lines.addVertex(pose, end).setColor(red, green, blue, 255)
                        .setNormal(pose, direction.x, direction.y, direction.z);
            }
        }
    }

    private static Vec3 debugCirclePoint(Vec3 center, int plane, double angle) {
        double first = java.lang.Math.cos(angle) * DEBUG_SPHERE_RADIUS;
        double second = java.lang.Math.sin(angle) * DEBUG_SPHERE_RADIUS;

        return switch (plane) {
            case 0 -> center.add(first, second, 0);
            case 1 -> center.add(first, 0, second);
            default -> center.add(0, first, second);
        };
    }

    private static Vector3f toPosePosition(Vec3 worldPosition, Vec3 cameraPosition, Matrix4f inversePose) {
        return inversePose.transformPosition(new Vector3f(
                (float)(worldPosition.x - cameraPosition.x),
                (float)(worldPosition.y - cameraPosition.y),
                (float)(worldPosition.z - cameraPosition.z)
        ));
    }
}
