#version 150

uniform sampler2D Sampler0;
uniform int Sides;
uniform float Time;
uniform float LineWidth;
uniform vec4 BorderColor;
uniform vec4 InnerColor;
uniform vec4 OutlineColor;
uniform vec4 TipColor;
uniform float Percent;

in vec2 texCoord;

out vec4 fragColor;

#define DISTORTION_STRENGTH 0.05
#define DISTORTION_SPEED 0.5
#define TIP_THRESHOLD 0.025
#define OUTLINE_THRESHOLD 0.05
#define PI 3.14159265359

// https://www.shadertoy.com/view/MtKcWW
// signed distance to a regular n-gon
float sdNGon(vec2 position, float radius, int N)
{
    float angle = (2 * PI)/float(N);
    float he = radius*tan(0.5*angle);

    // rotate to first sector
    float bn = angle*floor((atan(position.y,position.x)+0.5*angle)/angle);
    vec2  cs = vec2(cos(bn),sin(bn));
    position = mat2(cs.x,-cs.y,cs.y,cs.x)*position;

    // side of polygon
    return length(position-vec2(radius,clamp(position.y,-he,he)))*sign(position.x-radius);
}

float clampRadians(float angle) {
    return mod(angle + 2.0 * PI, 2.0 * PI);
}

vec2 pixelate(vec2 uv, float pixelSize) {
    return floor((uv / pixelSize) * 100) * (pixelSize / 100);
}

void main() {
    float distortionSpeed = DISTORTION_SPEED;
    float agitatedDistortionSpeed = DISTORTION_SPEED * 2.0;
    float tipThreshold = Percent - TIP_THRESHOLD > 0 ? TIP_THRESHOLD : Percent;
    // Calculate the percentage of the circle that should be filled
    vec2 pixelatedTexCoord = pixelate(texCoord, 4);
    float angle = atan(pixelatedTexCoord.y - 0.5, pixelatedTexCoord.x - 0.5) - PI / 2.0;
    float clampedAngle = clampRadians(angle);
    float percent = (clampedAngle / (2.0 * PI));
    vec4 borderColor = vec4(0.25);
    float percentDiff = Percent - percent;
    float tipLerp = 1.0;
    if (percentDiff + tipThreshold > 0.0) {
        if (percentDiff <= 0.0 && percentDiff + tipThreshold < 1.0) {
            tipLerp = min(1.0, 1.0 - (percentDiff + tipThreshold) / tipThreshold);
            borderColor = mix(BorderColor, borderColor, tipLerp);
        } else if (percentDiff > 0.0) {
            tipLerp = 0.0;
            borderColor = BorderColor;
        }
    }
    // Apply a few waves of distortion to the texture
    vec2 distortedTexCoord = pixelatedTexCoord;
    distortedTexCoord.x += DISTORTION_STRENGTH * sin(Time * distortionSpeed + pixelatedTexCoord.y * 10.0) + DISTORTION_STRENGTH * cos(Time * distortionSpeed * 0.2 + pixelatedTexCoord.x * 10.0);
    distortedTexCoord.y += DISTORTION_STRENGTH * cos(Time * distortionSpeed + pixelatedTexCoord.x * 10.0) + DISTORTION_STRENGTH * sin(Time * distortionSpeed * 0.2 + pixelatedTexCoord.y * 10.0);
    vec2 agitatedDistortedTexCoord = pixelatedTexCoord;
    agitatedDistortedTexCoord.x += DISTORTION_STRENGTH * sin(Time * agitatedDistortionSpeed + pixelatedTexCoord.y * 10.0) + DISTORTION_STRENGTH * cos(Time * agitatedDistortionSpeed * 0.2 + pixelatedTexCoord.x * 10.0);
    agitatedDistortedTexCoord.y += DISTORTION_STRENGTH * cos(Time * agitatedDistortionSpeed + pixelatedTexCoord.x * 10.0) + DISTORTION_STRENGTH * sin(Time * agitatedDistortionSpeed * 0.2 + pixelatedTexCoord.y * 10.0);
    vec2 finalDistortedTexCoord = mix(agitatedDistortedTexCoord, distortedTexCoord, tipLerp);
    vec4 texColor = texture(Sampler0, finalDistortedTexCoord) / 2 + 0.5; // Remap so that we don't end up with super dark spots
    texColor = texColor * borderColor;

    float innerRadius = 0.43 - LineWidth;
    float signedDistanceInner = sdNGon(pixelatedTexCoord - vec2(0.5, 0.5), innerRadius, Sides);
    float outerRadius = 0.43;
    float signedDistanceOuter = sdNGon(pixelatedTexCoord - vec2(0.5, 0.5), outerRadius, Sides);
    float border = (signedDistanceInner > 0.0 && signedDistanceOuter < 0.0) ? 1.0 : 0.0;
    float inside = (signedDistanceInner <= 0.0) ? 1.0 : 0.0;
    float outline = (signedDistanceOuter > -OUTLINE_THRESHOLD && signedDistanceOuter < 0) ? 1.0 : 0.0;

    fragColor = mix(mix(vec4(texColor.rgb, border), InnerColor, inside), OutlineColor, outline);
}
