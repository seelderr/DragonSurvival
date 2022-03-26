
import { promises as fs } from 'fs';
import * as path from "path";

async function saveJson(filename: string, content: object) {
    return await fs.writeFile(filename,JSON.stringify(content,null, 2),'utf8');
}


function generateSmallDoorBlockModel(doorMaterialName: string){
    return {
        "parent": "dragonsurvival:block/small_dragon_door",
        "textures": {
            "bottom": `dragonsurvival:block/${doorMaterialName}_small_dragon_door_bottom`,
            "top": `dragonsurvival:block/${doorMaterialName}_small_dragon_door_bottom`
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

async function main() {
    const projectRoot = [__dirname, '..','..'];
    const assetsRoot = [...projectRoot, 'src', 'main', 'resources', 'assets', 'dragonsurvival'];
    const dataRoot = [...projectRoot, 'src', 'main', 'resources', 'data', 'dragonsurvival'];

    const smallDoorMaterialNames = [
        "basalt",
        "acacia",
        "jungle",
        "forest",
        "oak",
        "birch",
        "diorite",
        "cave",
        "iron2",
        "warped",
        "sea",
        "iron",
        "spruce",
        "quartz",
        "dark_oak",
        "crimson"
    ];

    for (const smallDoorMaterialName of smallDoorMaterialNames) {
        await saveJson(path.join(...assetsRoot, 'models', 'block', `${smallDoorMaterialName}_small_dragon_door.json`), generateSmallDoorBlockModel(smallDoorMaterialName))
        await saveJson(path.join(...assetsRoot, 'models','block', `${smallDoorMaterialName}_small_dragon_door_hinge.json`), generateSmallDoorBlockModelHinge(smallDoorMaterialName))
        await saveJson(path.join(...assetsRoot, 'models','item', `${smallDoorMaterialName}_small_dragon_door.json`), generateSmallDoorItem(smallDoorMaterialName))
        await saveJson(path.join(...assetsRoot, 'blockstates', `${smallDoorMaterialName}_small_dragon_door.json`), generateSmallDoorBlockState(smallDoorMaterialName))
        await saveJson(path.join(...dataRoot, 'loot_tables', 'blocks', `${smallDoorMaterialName}_small_dragon_door.json `), generateSmallDoorLootTable(smallDoorMaterialName))
    }
}

main()