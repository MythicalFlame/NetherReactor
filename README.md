# NetherReactor Library
Allows custom content like armors and items to be added to Minecraft servers in a server-sided way, so that clients don't need to add client side mods (only a resource pack).  

## Usage
Add the release jar as a plugin on your server, and as a dependency for your new plugin (see below for more info).

### Version Guide
The plugin is being re-written to only support Paper. Along with this, the modding system is being completely rewritten. Mods for v0.7.x and v0.8.x are not compatible in any way.  
If you are still using Spigot, or are using older Paper versions that don't support the component API, you can use the 0.7.x versions. Note that 0.7.x versions are not supported and will not receive bug fixes.  

| Platform | Minecraft Version | NetherReactor Version |
|----------|-------------------|-----------------------|
| Spigot   | 1.18.2+           | v0.7.2                |
| Paper    | 1.18.2-1.21.3     | v0.7.2                |
| Paper    | 1.21.4+           | v0.8.0                |


## Developer Information
See the repository wiki for developer information. The wiki is maintained for the latest release version, and does not contain information about the beta releases or the dev branch.

## Future plans
### v0.8.x
Completed: paper rework, item API rework, tags, partial composting component implementation   

Plans for betas:  
Beta 2 - recipes with support for other custom item APIs, brigadier commands  

Beta 3 - automatic optional resource compilation for Bedrock/Java packs, enchantment support?  

Release - JEI integration with a separate mod, custom creative tab system for vanilla clients (like JEI), clean up code  

### Later
Custom block + entity support