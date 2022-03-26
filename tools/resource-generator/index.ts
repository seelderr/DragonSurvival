
import { promises as fs } from 'fs';
import * as path from "path";



async function saveJson(filename: string, content: object) {
    return await fs.writeFile(filename,JSON.stringify(content,null, 2),'utf8');
}


function generateSmallDoorBlock(doorMaterialName: string){
    return {
        "parent": "dragonsurvival:block/small_dragon_door",
        "textures": {
            "bottom": `dragonsurvival:block/${doorMaterialName}_small_dragon_door_bottom`,
            "top": `dragonsurvival:block/${doorMaterialName}_small_dragon_door_bottom`
        }
    };
}

const pathRoot = [__dirname, '..','..', 'src', 'main', 'resources', 'assets','dragonsurvival','models'];

let smallDoorMaterialNames = [
    'acacia',
];

for (const smallDoorMaterialName of smallDoorMaterialNames) {
    saveJson(path.join(
        ...pathRoot, 'block', `${smallDoorMaterialName}_small_dragon_door.json`
    ), generateSmallDoorBlock(smallDoorMaterialName))
}