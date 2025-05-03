# FortressGuns
Artillery and guns for MC!

This is a passion project of mine which I have been working on when my time permits.
Basically this means that I'm working on it occasionally; This project has a lower priority than my schoolwork and other things I gotta attend, so don't expect a set date for when there may be a release.
**For the gun enthusiasts: No, I'm not a gun expert, but I know enough to make some neat mechanics.**


This is an artillery plugin for Minecraft Version 1.17.1 which adds a variety of artillery to the game. We currently have 6 guns implemented with several more incoming, most likely after the first release.
Each gun is placed into the world, can be operated, reloaded, and shot. Custom entities are used to model the guns, which use procedural animation to revolve around and fire. They also have their own health pool, 
damage resistances and vulnerabilities, and fire projectiles which produce custom death messages for players. 

The plugin is completely configurable down to each gun, as well as general options available.

Current Artillery Implemented:
---

### Field Light
This is a small artillery piece with short range, but a faster reload time to compensate. It is only capable of firing standard shells.

### Field Heavy
This is a large artillery piece with a longer range, but longer reload time. It has higher health than the field light, and can fire both standard and high explosive shells.

### Heavy Flak
Made to destroy airborne targets, this gun has a faster swivel speed and programmable shells which can be set to explode after a certain time. Because it is not suited for ground targets,
this gun cannot aim lower than the horizon line and can only be loaded with flak shells.

### Missile launcher
Modelled after Surface to Air launchers, the main use of this piece is to target fast moving aerial targets such as players using elytra. The missiles use adaptive speed and acceleration to catch these targets, 
meaning that targeted players cannot simply outfly these with rockets and instead must use evasive maneuvers or flares to survive. This adaptive speed and acceleration helps to balance out the rocket's movement, meaning that slower flying players will still have time to react, and faster moving players won't be able to escape as easily- But the missiles don't simply fly towards the player; Instead they'll use a variety of approach angles to try and intercept the target before they run out of fuel.

### Heavy Machine gun
Technically not an artillery piece, but I thought it would be cool to also model and bring into the game. The heavy machine gun rapidly fires bullets which can deflect projectiles such as other artillery shells and vanilla projectiles like fireballs and TNT. These projectiles account for gravity and air resistance, meaning you'll need to lead your shots in order to hit moving targets which are far away.
Because this gun fires rapidly, it suffers from overheating (configurable) and may also jam (also configurable). However, because machineguns in real life are aimed via the operator, and not a mechanical mechanism, this gun has full freedom of movement that is only restricted by the player's ability to look around.

### Light Flak
Similar to the heavy machine gun, but fires bursts of projectiles which explode on impact. The light flak also suffers from overheating issues and jamming issues. 

Content to complete before first release
---
- [ ] Custom Explosions
    - [X] Phase 1 - Planning and research
    - [ ] Phase 2 - Projectile Overhaul  < Current focus
    - [ ] Phase 3 - Explosion Overhaul
- [X] Artillery operation mechanics
- [ ] Bugfixing and polishing for some specific mechanics
- [ ] Survival friendly ways to go about obtaining the artillery (Either recipes or something else)
- [ ] Flares

Planned Content
---

- Railgun (Probably a more Sci-fi version, cause real-world ones are basically just a heavier Field Heavy)
- CRAM (+ Radar towers)
- Laser machinegun (The type of stuff you see from Sci-fi films which fire a burst of lasers)
- ICBMs and Nukes <- We'll see how particles play out, otherwise this might be challenging


Will I be updating to newer Minecraft versions?
Probably not- With the limited time I already have to make content for this plugin, If I dedicate even more time to port it to newer versions, I won't have time to add all of the content I'd like to put in.
Although it is an intriguing prospect to consider... I might change my mind on this some time.


Why has implementation taken so long?
This is a project which as I've mentioned above, has a low ranking on my priority list. First comes school and work and then other projects which have a set due date. I'm also the only
person who is working on this project. 



