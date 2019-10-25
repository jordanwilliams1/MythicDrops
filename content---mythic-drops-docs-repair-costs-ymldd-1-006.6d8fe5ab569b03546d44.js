(window.webpackJsonp=window.webpackJsonp||[]).push([[15],{163:function(e,t,n){"use strict";n.r(t),n.d(t,"frontMatter",(function(){return i})),n.d(t,"rightToc",(function(){return o})),n.d(t,"default",(function(){return u}));n(58),n(31),n(22),n(23),n(59),n(0);var r=n(179);function a(){return(a=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var r in n)Object.prototype.hasOwnProperty.call(n,r)&&(e[r]=n[r])}return e}).apply(this,arguments)}var i={id:"repairCosts_yml",title:"repairCosts.yml"},o=[{value:"Configuration",id:"configuration",children:[]}],s={rightToc:o},c="wrapper";function u(e){var t=e.components,n=function(e,t){if(null==e)return{};var n,r,a={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,["components"]);return Object(r.b)(c,a({},s,n,{components:t,mdxType:"MDXLayout"}),Object(r.b)("h2",{id:"configuration"},"Configuration"),Object(r.b)("p",null,"MythicDrops has a lot of configuration options. Below is the shortened contents of the\nrepairCosts.yml with inline explanations of what each configuration option does."),Object(r.b)("pre",null,Object(r.b)("code",a({parentName:"pre"},{className:"language-yaml"}),"version: 1.0.0\n## Identifier for an item that can be repaired. Needs to be unique.\n## Does NOT have to be anything sensible, it can be gibberish like\n## asijsoiadsnansga.\nwooden-axe:\n  ## Name of the material of the item that can be repaired.\n  ## https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html\n  material-name: WOODEN_AXE\n  ## You can specify an item name to create custom repair costs for items\n  ## with a given name. This defaults to being undefined and that's usually\n  ## fine for most usages.\n  item-name: Example\n  ## Behaves identically to item-name, but with the item's lore instead.\n  item-lore:\n    - Example\n  ## Costs to repair this item.\n  costs:\n    ## Identifier for a repair cost. Needs to be unique per costs section.\n    ## You can have multiple.\n    ## Does NOT have to be anything sensible, it can be gibberish like\n    ## asijsoiadsnansga.\n    default:\n      ## Name of the material of the item that is required to do the repairing.\n      ## https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html\n      material-name: OAK_WOOD\n      ## Priority of this particular cost. If you have multiple matching costs\n      ## in your inventory, the one with the highest valued priority wins (1 > 0).\n      priority: 0\n      ## How many of this item are required to perform the repair?\n      amount: 1\n      ## How much experience is required to perform the repair?\n      experience-cost: 0\n      ## What percentage of the item's durability should be repaired?\n      repair-per-cost: 0.1\n      ## The item used to perform the repair must have a name matching this\n      ## value if it is defined.\n      ## This defaults to being undefined and that's usually\n      ## fine for most usages.\n      item-name: Example\n      ## Behaves identically to item-name, but with the item's lore instead.\n      item-lore:\n        - Example\n# other repair costs down here...\n")))}u.isMDXComponent=!0},179:function(e,t,n){"use strict";n.d(t,"a",(function(){return s})),n.d(t,"b",(function(){return p}));var r=n(0),a=n.n(r),i=a.a.createContext({}),o=function(e){var t=a.a.useContext(i),n=t;return e&&(n="function"==typeof e?e(t):Object.assign({},t,e)),n},s=function(e){var t=o(e.components);return a.a.createElement(i.Provider,{value:t},e.children)};var c="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return a.a.createElement(a.a.Fragment,{},t)}},l=Object(r.forwardRef)((function(e,t){var n=e.components,r=e.mdxType,i=e.originalType,s=e.parentName,c=function(e,t){var n={};for(var r in e)Object.prototype.hasOwnProperty.call(e,r)&&-1===t.indexOf(r)&&(n[r]=e[r]);return n}(e,["components","mdxType","originalType","parentName"]),l=o(n),p=r,m=l[s+"."+p]||l[p]||u[p]||i;return n?a.a.createElement(m,Object.assign({},{ref:t},c,{components:n})):a.a.createElement(m,Object.assign({},{ref:t},c))}));function p(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=n.length,o=new Array(i);o[0]=l;var s={};for(var u in t)hasOwnProperty.call(t,u)&&(s[u]=t[u]);s.originalType=e,s[c]="string"==typeof e?e:r,o[1]=s;for(var p=2;p<i;p++)o[p]=n[p];return a.a.createElement.apply(null,o)}return a.a.createElement.apply(null,n)}l.displayName="MDXCreateElement"}}]);