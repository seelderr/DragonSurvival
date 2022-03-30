
import { promises as fs } from 'fs';
import * as path from "path";

async function saveJson(filename: string, content: object) {
    return await fs.writeFile(filename,JSON.stringify(content,null, 2),'utf8');
}


function generateSmallDoorBlockModel(doorMaterialName: string){
    return {
        "parent": "dragonsurvival:block/small_dragon_door",
        "textures": {
            "bottom": `dragonsurvival:block/${doorMaterialName}_small_dragon_door`,
            "top": `dragonsurvival:block/${doorMaterialName}_small_dragon_door`
        }
    };
}

function generateSmallDoorBlockModelHinge(doorMaterialName: string){
    return {
        "parent": "dragonsurvival:block/small_dragon_door_rh",
        "textures": {
            "bottom": `dragonsurvival:block/${doorMaterialName}_small_dragon_door`,
            "top": `dragonsurvival:block/${doorMaterialName}_small_dragon_door`
        }
    }
}


function generateSmallDoorItem(doorMaterialName: string){
    return {
        "parent": "item/generated",
        "textures": {
            "layer0": `dragonsurvival:block/${doorMaterialName}_small_dragon_door`
        }
    };
}

function generateSmallDoorBlockState(doorMaterialName: string){
    return {
        "variants": {
            "facing=east,hinge=left,open=false": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door`
            },
            "facing=south,hinge=left,open=false": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door`,
                "y": 90
            },
            "facing=west,hinge=left,open=false": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door`,
                "y": 180
            },
            "facing=north,hinge=left,open=false": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door`,
                "y": 270
            },
            "facing=east,hinge=right,open=false": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door_hinge`
            },
            "facing=south,hinge=right,open=false": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door_hinge`,
                "y": 90
            },
            "facing=west,hinge=right,open=false": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door_hinge`,
                "y": 180
            },
            "facing=north,hinge=right,open=false": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door_hinge`,
                "y": 270
            },
            "facing=east,hinge=left,open=true": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door_hinge`,
                "y": 90
            },
            "facing=south,hinge=left,open=true": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door_hinge`,
                "y": 180
            },
            "facing=west,hinge=left,open=true": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door_hinge`,
                "y": 270
            },
            "facing=north,hinge=left,open=true": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door_hinge`
            },
            "facing=east,hinge=right,open=true": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door`,
                "y": 270
            },
            "facing=south,hinge=right,open=true": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door`
            },
            "facing=west,hinge=right,open=true": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door`,
                "y": 90
            },
            "facing=north,hinge=right,open=true": {
                "model": `dragonsurvival:block/${doorMaterialName}_small_dragon_door`,
                "y": 180
            }
        }
    }
}

function generateSmallDoorLootTable(doorMaterialName: string){
    return {
        "type": "minecraft:block",
        "pools": [
            {
                "rolls": 1,
                "entries": [
                    {
                        "type": "minecraft:item",
                        "name": `dragonsurvival:${doorMaterialName}_small_dragon_door`
                    }
                ],
                "conditions": [
                    {
                        "condition": "minecraft:survives_explosion"
                    }
                ]
            }
        ]
    }
}

function generateSmallDoorRecipe(doorMaterialName: string){
    return {
        "type": "minecraft:crafting_shapeless",
        "ingredients": [
            {
                "item": `dragonsurvival:${doorMaterialName}_dragon_door`
            },
        ],
        "result": {
            "item": `dragonsurvival:${doorMaterialName}_small_dragon_door`,
            "count": 3
        }
    }
}

async function main() {
    const projectRoot = [__dirname, '..','..'];
    const assetsRoot = [...projectRoot, 'src', 'main', 'resources', 'assets', 'dragonsurvival'];
    const dataRoot = [...projectRoot, 'src', 'main', 'resources', 'data', 'dragonsurvival'];

    // generateRecipe, only switch to true if large version of the door is available
    const smallDoorVariants = [
        {doorMaterial: "acacia", generateRecipe: true},
        {doorMaterial: "jungle", generateRecipe: true},
        {doorMaterial: "oak", generateRecipe: true},
        {doorMaterial: "dark_oak", generateRecipe: true},
        {doorMaterial: "birch", generateRecipe: true},
        {doorMaterial: "spruce", generateRecipe: true},
        {doorMaterial: "stone",  generateRecipe: true},
        {doorMaterial: "sleeper", generateRecipe: true},
        {doorMaterial: "warped", generateRecipe: true},
        {doorMaterial: "cave", generateRecipe: true},
        {doorMaterial: "forest", generateRecipe: true},
        {doorMaterial: "sea", generateRecipe: true},
        {doorMaterial: "iron", generateRecipe: true},
        {doorMaterial: "murderer", generateRecipe: true},
        {doorMaterial: "crimson", generateRecipe: true },
    ];

    for (const smallDoorVariant of smallDoorVariants) {
        await saveJson(path.join(...assetsRoot, 'models', 'block', `${smallDoorVariant.doorMaterial}_small_dragon_door.json`), generateSmallDoorBlockModel(smallDoorVariant.doorMaterial))
        await saveJson(path.join(...assetsRoot, 'models','block', `${smallDoorVariant.doorMaterial}_small_dragon_door_hinge.json`), generateSmallDoorBlockModelHinge(smallDoorVariant.doorMaterial))
        await saveJson(path.join(...assetsRoot, 'models','item', `${smallDoorVariant.doorMaterial}_small_dragon_door.json`), generateSmallDoorItem(smallDoorVariant.doorMaterial))
        await saveJson(path.join(...assetsRoot, 'blockstates', `${smallDoorVariant.doorMaterial}_small_dragon_door.json`), generateSmallDoorBlockState(smallDoorVariant.doorMaterial))
        await saveJson(path.join(...dataRoot, 'loot_tables', 'blocks', `${smallDoorVariant.doorMaterial}_small_dragon_door.json`), generateSmallDoorLootTable(smallDoorVariant.doorMaterial))
        if (smallDoorVariant.generateRecipe) {
            await saveJson(path.join(...dataRoot, 'recipes', `${smallDoorVariant.doorMaterial}_small_dragon_door.json`), generateSmallDoorRecipe(smallDoorVariant.doorMaterial))
        }
    }
}

main()