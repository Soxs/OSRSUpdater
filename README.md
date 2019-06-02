# OSRSUpdater
A hook updater for OSRS which uses bytecode patterns and ASM for identification.  
Probably no longer works, but could be a good resource for someone trying to learn.  
Credits: Kevboi

# Example Log:
(which is quite old, it now has deobfuscation etc)

```
Starting OSRS Updater.
Finished loading 230 classes in 671ms from: gamepack#103.jar
Stored 1113 multipliers

[- Node identified as: gj extends Object -](3/3)
	[> 'getNext()' identified as 'gj.eo' -] (Lgj;)
	[> 'getPrevious()' identified as 'gj.ee' -] (Lgj;)
	[> 'getHash()' identified as 'gj.ec' -] (J)

[- CacheableNode identified as: gt extends gj -](2/2)
	[> 'getNext()' identified as 'gt.ci' -] (Lgt;)
	[> 'getPrevious()' identified as 'gt.cp' -] (Lgt;)

[- HashTable identified as: gy extends Object -](1/1)
	[> 'getCache()' identified as 'gy.p' -] ([Lgj;)

[- LinkedList identified as: gl extends Object -](2/2)
	[> 'getHead()' identified as 'gl.d' -] (Lgj;)
	[> 'getCurrent()' identified as 'gl.p' -] (Lgj;)

[- Queue identified as: gw extends Object -](1/1)
	[> 'getCacheableNode()' identified as 'gw.d' -] (Lgt;)

[- Cache identified as: gu extends Object -](4/5)
	[> 'getHashTable()' identified as 'gu.l' -] (Lgy;)
	[> 'getEmptyCacheableNode()' identified as 'gu.d' -] (Lgt;)
	[> 'getRemaining()' identified as 'gu.p' -] (I)	[ * 1 ]
	[> 'getSize()' identified as 'gu.v' -] (I)	[ * 1 ]

[- Renderable identified as: ch extends gt -](1/1)
	[> 'getModelHeight()' identified as 'ch.cf' -] (I)	[ * 1812947537 ]

[- CollisionData identified as: dl extends Object -](1/3)
	[> 'getFlags()' identified as 'dl.aj' -] ([[I)

[- Actor identified as: aj extends ch -](9/19)
	[> 'isAnimating()' identified as 'aj.al' -] (Z)
	[> 'getSpokenText()' identified as 'aj.ac' -] (LString;)
	[> 'getGridX()' identified as 'aj.ax' -] (I)	[ * -2013728192 ]
	[> 'getGridY()' identified as 'aj.bx' -] (I)	[ * 2137051008 ]
	[> 'getHitsplatsDamage()' identified as 'aj.ay' -] ([I)
	[> 'getHitsplatsTypes()' identified as 'aj.ag' -] ([I)
	[> 'getMaxHealth' identified as 'aj.bi' -] (I)	[ * -1324479051 ]
	[> 'getGameCycle()' identified as 'client.r' -] (I)	[ * -1040073859 ]
	[> 'getLoopCycle()' identified as 'aj.bs' -] (I)	[ * -1158366777 ]

[- Projectile identified as: u extends ch -](3/3)
	[> 'getProjectileComposite()' identified as 'u.s' -] (Laf;)
	[> 'isMoving()' identified as 'u.m' -] (Z)
	[> 'getDuration()' identified as 'u.g' -] (I)	[ * -46564401 ]

[- ProjectileComposite identified as: af extends gt -](1/1)
	[> 'isMoving()' identified as 'af.m' -] (Z)

[- Item identified as: ak extends ch -](2/2)
	[> 'getId()' identified as 'ak.d' -] (I)	[ * -848428919 ]
	[> 'getStackSize()' identified as 'ak.p' -] (I)	[ * 1308808435 ]

[- ItemComposite identified as: at extends gt -](5/5)
	[> 'getStackIds()' identified as 'at.f' -] (I)	[ * 1303294175 ]
	[> 'getName()' identified as 'at.k' -] (LString;)
	[> 'isMember()' identified as 'at.ae' -] (Z)
	[> 'getActions()' identified as 'at.ak' -] ([LString;)
	[> 'getGroundActions()' identified as 'at.ao' -] ([LString;)

[- Model identified as: dn extends ch -](6/6)
	[> 'getVerticesX()' identified as 'dn.w' -] ([I)
	[> 'getVerticesY()' identified as 'dn.u' -] ([I)
	[> 'getVerticesZ()' identified as 'dn.a' -] ([I)
	[> 'getIndicesX()' identified as 'dn.b' -] ([I)
	[> 'getIndicesY()' identified as 'dn.o' -] ([I)
	[> 'getIndicesZ()' identified as 'dn.m' -] ([I)

[- GameObject identified as: ct extends Object -](2/2)
	[> 'getRenderable()' identified as 'ct.y' -] (Lch;)
	[> 'getId()' identified as 'ct.x' -] (I)	[ * 714123667 ]

[- GameObjectComposite identified as: ac extends gt -](4/4)
	[> 'getName()' identified as 'ac.m' -] (LString;)
	[> 'getActions()' identified as 'ac.ak' -] ([LString;)
	[> 'getWidth()' identified as 'ac.z' -] (I)	[ * -1976023901 ]
	[> 'getHeight()' identified as 'ac.t' -] (I)	[ * 1162660975 ]

[- Player identified as: v extends aj -](7/7)
	[> 'getName()' identified as 'v.w' -] ([LString;)
	[> 'getModel()' identified as 'v.n' -] (Ldn;)
	[> 'getPlayerComposite()' identified as 'v.p' -] (Lfz;)
	[> 'isVisible()' identified as 'v.i' -] (Z)
	[> 'getCombatLevel()' identified as 'v.u' -] (I)	[ * 1614853309 ]
	[> 'getSkullIcon()' identified as 'v.o' -] (I)	[ * 959962901 ]
	[> 'getPrayerIcon()' identified as 'v.v' -] (I)	[ * 1744423653 ]

[- PlayerComposite identified as: fz extends Object -](2/2)
	[> 'getEquipment()' identified as 'fz.p' -] ([I)
	[> 'isFemale()' identified as 'fz.v' -] (Z)

[- Npc identified as: aw extends aj -](1/1)
	[> 'getComposite()' identified as 'aw.d' -] (Lar;)

[- NpcComposite identified as: ar extends gt -](4/4)
	[> 'getActions()' identified as 'ar.q' -] ([LString;)
	[> 'getName()' identified as 'ar.w' -] (LString;)
	[> 'getId()' identified as 'ar.y' -] (I)	[ * -2095462435 ]
	[> 'getModelIds()' identified as 'ar.al' -] ([I)

[- Widget identified as: fg extends gj -](21/26)
	[> 'getIndex()' identified as 'fg.ao' -] (I)	[ * 1645211541 ]
	[> 'getName()' identified as 'fg.cl' -] (LString;)
	[> 'getItemStackSize()' identified as 'fg.eg' -] (I)	[ * 1393082105 ]
	[> 'getItemId()' identified as 'fg.ey' -] (I)	[ * 813479615 ]
	[> 'getScrollX()' identified as 'fg.ad' -] (I)	[ * -352661099 ]
	[> 'getScrollY()' identified as 'fg.aw' -] (I)	[ * -1602694527 ]
	[> 'getSlotStackSizes()' identified as 'fg.dr' -] ([I)
	[> 'isHidden()' identified as 'fg.az' -] (Z)
	[> 'getId()' identified as 'fg.x' -] (I)	[ * -1536575275 ]
	[> 'getActions()' identified as 'fg.ch' -] ([LString;)
	[> 'getParentId()' identified as 'fg.ba' -] (I)	[ * 686060225 ]
	[> 'getBorderThickness()' identified as 'fg.an' -] (I)	[ * -357503007 ]
	[> 'getTextureId()' identified as 'fg.au' -] (I)	[ * -131734905 ]
	[> 'getType()' identified as 'fg.n' -] (I)	[ * -1305917269 ]
	[> 'getText()' identified as 'fg.cl' -] (LString;)
	[> 'getHeight()' identified as 'fg.f' -] (I)	[ * 507570867 ]
	[> 'getWidth()' identified as 'fg.g' -] (I)	[ * 124195285 ]
	[> 'getSlotContentIds()' identified as 'fg.da' -] ([I)
	[> 'getChildren()' identified as 'fg.ez' -] ([Lfg;)
	[> 'getParent()' identified as 'fg.cc' -] (Lfg;)
	[> 'getOpcodes()' identified as 'fg.dg' -] ([[I)

[- WidgetNode identified as: d extends gj -](1/1)
	[> 'getId()' identified as 'd.l' -] (I)	[ * 1518911111 ]

[- Region identified as: cc extends Object -](3/3)
	[> 'getGameObjects()' identified as 'cc.e' -] ([Lct;)
	[> 'getGameObjectCache()' identified as 'cc.al' -] ([Lct;)
	[> 'getTiles()' identified as 'cc.w' -] ([[[Lcy;)

[- WallObject identified as: cq extends Object -](3/3)
	[> 'getRenderable()' identified as 'cq.w' -] (Lch;)
	[> 'getRenderable2()' identified as 'cq.u' -] (Lch;)
	[> 'getId()' identified as 'cq.a' -] (I)	[ * 785572969 ]

[- FloorObject identified as: cb extends Object -](2/2)
	[> 'getRenderable()' identified as 'cb.l' -] (Lch;)
	[> 'getId()' identified as 'cb.y' -] (I)	[ * 827739875 ]

[- Tile identified as: cy extends gj -](6/6)
	[> 'getGameObjects()' identified as 'cy.m' -] ([Lct;)
	[> 'getWallDecoration()' identified as 'cy.u' -] (Lcq;)
	[> 'getFloorDecoration()' identified as 'cy.e' -] (Lcb;)
	[> 'getPlane()' identified as 'cy.d' -] (I)	[ * -1839515449 ]
	[> 'getX()' identified as 'cy.p' -] (I)	[ * 1489288579 ]
	[> 'getY()' identified as 'cy.v' -] (I)	[ * -648052847 ]

[- Client identified as: client extends ea -](26/40)
	[> 'getWidgetNode()' identified as 'client.io' -] (Lgy;)
	[> 'getNpcs()' identified as 'client.cv' -] ([Law;)
	[> 'getPlayers()' identified as 'client.gi' -] ([Lv;)
	[> 'getGroundItems()' identified as 'client.hd' -] ([[[Lgl;)
	[> 'getProjectiles()' identified as 'client.hn' -] (Lgl;)
	[> 'getCollisionMaps()' identified as 'client.w' -] ([Ldl;)
	[> 'isItemSelected()' identified as 'client.o' -] (Z)
	[> 'getCompassAngle()' identified as 'client.ei' -] (I)	[ * -1718342721 ]
	[> 'getRegion()' identified as 'l.dj' -] (Lcc;)
	[> 'getBaseX()' identified as 'dy.a' -] ([I)
	[> 'getTileHeights()' identified as 'w.d' -] ([[[I)
	[> 'getPlane()' identified as 'l.gb' -] (I)	[ * -747958745 ]
	[> 'getSettings()' identified as 'client.m' -] ([I)
	[> 'getWidgetPositionsY()' identified as 'client.lo' -] ([I)
	[> 'getWidgetPositionsX()' identified as 'client.lt' -] ([I)
	[> 'getSelectedItemName()' identified as 'client.it' -] (LString;)
	[> 'getSkillLevelArray()' identified as 'client.lt' -] ([I)
	[> 'getRealSkillLevelArray()' identified as 'client.hb' -] ([I)
	[> 'getSkillExpArray()' identified as 'client.hz' -] ([I)
	[> 'getMenuOptionCount()' identified as 'client.hq' -] (I)	[ * 1768430155 ]
	[> 'getCameraPitch()' identified as 'ek.fo' -] (I)	[ * 1162853107 ]
	[> 'getCameraYaw()' identified as 'eh.fs' -] (I)	[ * -210812059 ]
	[> 'getTileBytes()' identified as 'w.p' -] ([[[
	[> 'getPlayerSettings()' identified as 'fy.v' -] ([I)
	[> 'getLocalPlayer()' identified as 'el.hw' -] (Lv;)
	[> 'getWidgets()' identified as 'fg.d' -] ([[Lfg;)
	[> Method 'getItemComposite()' identified as 'z.d' -] ((IB)Lat;)
	[> Method 'getObjectComposite()' identified as 'r.p' -] ((II)Lac;)

[- Mouse identified as: eu extends Object -](0/0)

[- Keyboard identified as: ep extends Object -](0/0)

Identified 123/155 fields
Identified 29/29 classes
Also, identified 2 methods
Finished analyzing in 2568 ms
```
