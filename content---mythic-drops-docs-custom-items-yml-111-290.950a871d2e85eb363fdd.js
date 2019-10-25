(window.webpackJsonp=window.webpackJsonp||[]).push([[8],{149:function(e,t,n){"use strict";n.r(t),n.d(t,"frontMatter",(function(){return i})),n.d(t,"rightToc",(function(){return r})),n.d(t,"default",(function(){return c}));n(58),n(31),n(22),n(23),n(59),n(0);var o=n(179);function a(){return(a=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var o in n)Object.prototype.hasOwnProperty.call(n,o)&&(e[o]=n[o])}return e}).apply(this,arguments)}var i={id:"customItems_yml",title:"customItems.yml"},r=[{value:"Configuration",id:"configuration",children:[]}],s={rightToc:r},m="wrapper";function c(e){var t=e.components,n=function(e,t){if(null==e)return{};var n,o,a={},i=Object.keys(e);for(o=0;o<i.length;o++)n=i[o],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,["components"]);return Object(o.b)(m,a({},s,n,{components:t,mdxType:"MDXLayout"}),Object(o.b)("h2",{id:"configuration"},"Configuration"),Object(o.b)("p",null,"MythicDrops has a lot of configuration options. Below is the contents of the\ncustomItems.yml with inline explanations of what each configuration option does."),Object(o.b)("pre",null,Object(o.b)("code",a({parentName:"pre"},{className:"language-yaml"}),'version: 5.0.0\n## Name of a custom item. Used as an identifier by the plugin, so it needs\n## to be unique.\nsocketsword:\n  ## Material of the custom item. You can obtain a list of potential material\n  ## names (for 1.14 at time of writing) here:\n  ## https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html\n  material: WOODEN_SWORD\n  ## Name to display on the custom item itself.\n  display-name: "&2Socket Sword (Unique)"\n  ## Goes in the description of the custom item.\n  lore:\n    - "&7This sword has sockets!"\n    - "&2(Socket)"\n    - "&2(Socket)"\n    - "&2(Socket)"\n    - "&7Find a &2Socket Gem&7 to fill a &2(Socket)"\n  ## Weight of the custom item. See the weights section of the documentation\n  ## for more information.\n  weight: 100\n  ## Durability of the item when spawned. A durability of 1 means that the\n  ## item has taken 1 damage. If an item has a max durability of 127,\n  ## then setting this to 126 would almost break the item. This is due\n  ## to how both Minecraft and Spigot handle durability.\n  durability: 1\n  ## Chance for this custom item to drop when a monster that is carrying it\n  ## dies.\n  chance-to-drop-on-monster-death: 1.0\n  ## Should a message be sent to every player in the same world\n  ## when this custom item is dropped?\n  broadcast-on-find: true\n  ## Custom model data value. Only supported in 1.14+.\n  ## https://www.planetminecraft.com/forums/communities/texturing/new-1-14-custom-item-models-tuto-578834/\n  custom-model-data: 0\n  ## Should the custom item have the unbreakable NBT tag?\n  unbreakable: false\n  ## Enchantments to go on the custom item. Enchantment names here:\n  ## https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html\n  enchantments:\n    ## Standard enchantment name to level mapping.\n    DAMAGE_ALL: 5\n    LOOTING:\n      ## Minimum level of enchantment\n      minimum-level: 1\n      ## Maximum level of enchantment. Can be higher than normal maximum level\n      ## per Minecraft rules.\n      maximum-level: 2\n')))}c.isMDXComponent=!0},179:function(e,t,n){"use strict";n.d(t,"a",(function(){return s})),n.d(t,"b",(function(){return l}));var o=n(0),a=n.n(o),i=a.a.createContext({}),r=function(e){var t=a.a.useContext(i),n=t;return e&&(n="function"==typeof e?e(t):Object.assign({},t,e)),n},s=function(e){var t=r(e.components);return a.a.createElement(i.Provider,{value:t},e.children)};var m="mdxType",c={inlineCode:"code",wrapper:function(e){var t=e.children;return a.a.createElement(a.a.Fragment,{},t)}},u=Object(o.forwardRef)((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,s=e.parentName,m=function(e,t){var n={};for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&-1===t.indexOf(o)&&(n[o]=e[o]);return n}(e,["components","mdxType","originalType","parentName"]),u=r(n),l=o,h=u[s+"."+l]||u[l]||c[l]||i;return n?a.a.createElement(h,Object.assign({},{ref:t},m,{components:n})):a.a.createElement(h,Object.assign({},{ref:t},m))}));function l(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,r=new Array(i);r[0]=u;var s={};for(var c in t)hasOwnProperty.call(t,c)&&(s[c]=t[c]);s.originalType=e,s[m]="string"==typeof e?e:o,r[1]=s;for(var l=2;l<i;l++)r[l]=n[l];return a.a.createElement.apply(null,r)}return a.a.createElement.apply(null,n)}u.displayName="MDXCreateElement"}}]);