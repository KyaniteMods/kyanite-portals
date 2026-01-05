# Kyanite Portals

An API that allows you to create data-driven portals (beta)

## Creating a simple Nether portal
You can use `SimplePortalBuilder` to create a simple Nether-like portal. Here's an example of a red portal to the End:

```java
SimplePortalBuilder.create()
        .ignition(Items.EMERALD)
        .ignition(Blocks.EMERALD_BLOCK)
        .frame(Blocks.OBSIDIAN)
        .color(0xFF0000)
        .fromDimension(LevelStem.OVERWORLD)
        .toDimension(LevelStem.END)
        .register(Identifier.fromNamespaceAndPath("example", "end_portal"));
```

`SimplePortalBuilder` injects into the registry. Here is the same portal using JSON in 1.21.11:

```json
{
  "generator": {
    "type": "kyanite_portals:nether_like",
    "valid_in": ["minecraft:overworld", "minecraft:the_end"],
    "triggers": [
      {
        "type": "kyanite_portals:use_item",
        "predicate": {
          "items": ["minecraft:emerald"]
        },
        "damage_item_by": 1
      },
      {
        "type": "kyanite_portals:block_change",
        "predicate": {
          "blocks": ["minecraft:emerald_block"]
        }
      }
    ],
    "portal_block": {
      "block": "kyanite_portals:custom_portal",
      "nbt": "{portal:\"example:end_portal\"}"
    }
  },
  "tester": {
    "type": "kyanite_portals:rectangle",
    "frame": {
      "blocks": ["minecraft:obsidian"]
    },
    "replaceable": {
      "blocks": ["minecraft:air", "minecraft:cave_air", "minecraft:void_air", "minecraft:emerald_block"]
    },
    "portal": {
      "blocks": ["kyanite_portals:custom_portal"],
      "nbt": "{portal:\"kyanite_portals:nether_portal\"}"
    },
    "width": { "min": 4, "max": 23 },
    "height": { "min": 5, "max": 23 },
    "corners_required": false,
    "axes": ["x", "z"]
  },
  "travel_time": {
    "game_modes": {
      "creative": 0,
      "survival": 80,
      "adventure": 80,
      "spectator": 0
    },
    "non_player": 0,
    "default_time": 80
  },
  "enter_actions": [
    {
      "action": "kyanite_portals:play_local_sound",
      "sound_event": {
        "sound_id": "minecraft:block.portal.trigger"
      },
      "volume": 0.25,
      "pitch": {
        "type": "minecraft:uniform",
        "min_inclusive": 0.8,
        "max_exclusive": 1.2
      }
    }
  ],
  "travel_actions": [
    {
      "action": "kyanite_portals:store_action_location",
      "key": "location",
      "settings": {
        "location_options": {
          "dimension": {
            "first": ["minecraft:overworld"],
            "second": ["minecraft:the_end"],
            "in": "opposite_point"
          },
          "position": {
            "from": "entity"
          }
        }
      }
    },
    {
      "action": "kyanite_portals:teleport_to_nether_like_portal_poi",
      "point_of_interest_types": "#kyanite_portals:custom_portals",
      "portal_predicate": {
        "blocks": ["kyanite_portals:custom_portal"],
        "nbt": "{portal:\"example:end_portal\"}"
      },
      "settings": {
        "location_options": {
          "load": "location"
        },
        "on_failure": [
          {
            "action": "kyanite_portals:create_nether_like_portal",
            "settings": {
              "predicate": {
                "type_specific": {
                  "type": "player"
                }
              },
              "location_options": {
                "load": "location"
              }
            },
            "frame_block": "minecraft:obsidian",
            "portal_block": {
              "block": "kyanite_portals:custom_portal",
              "nbt": "{portal:\"example:end_portal\"}"
            },
            "size": {
              "width": 14,
              "height": 9
            }
          }
        ]
      },
      "search_range": 128
    },
    {
      "action": "kyanite_portals:play_local_sound",
      "settings": {
        "location_options": {
          "dimension": {
            "in": "entity_dimension"
          },
          "position": {
            "from": "entity"
          }
        }
      },
      "sound_event": {
        "sound_id": "minecraft:block.portal.travel"
      },
      "volume": 0.25,
      "pitch": {
        "type": "minecraft:uniform",
        "min_inclusive": 0.8,
        "max_exclusive": 1.2
      }
    }
  ],
  "animation_tick_actions": [
    {
      "action": "kyanite_portals:play_local_sound",
      "settings": {
        "environment": "client",
        "probability": 0.01,
        "location_options": {
          "dimension": {
            "in": "entry_point"
          },
          "position": {
            "from": "portal"
          }
        }
      },
      "sound_event": {
        "sound_id": "minecraft:block.portal.ambient"
      },
      "volume": 0.5,
      "pitch": {
        "type": "minecraft:uniform",
        "min_inclusive": 0.8,
        "max_exclusive": 1.2
      }
    },
    {
      "action": "kyanite_portals:spawn_nether_like_portal_particles",
      "settings": {
        "environment": "client"
      },
      "amount": 4,
      "particle_options": {
        "type": "kyanite_portals:portal",
        "color": [1.0, 0.0, 0.0]
      }
    }
  ]
}
```
Note that in this case, the color of the custom portal will not be registered, therefore, this method is better for your own block.

## Portal effects
Portals may have client-side effects which are added to the directory `assets/example/portal_effect/end_portal.json` for a portal with ID `example:end_portal`. Here's an example definition:
```json
{
  "values": [
    {
      "type": "kyanite_portals:nausea"
    },
    {
      "type": "kyanite_portals:close_screens"
    },
    {
      "type": "kyanite_portals:loading_background/nether_like",
      "atlas": "minecraft:blocks",
      "texture": "minecraft:block/nether_portal",
      "tint": 16777215
    },
    {
      "type": "kyanite_portals:texture_overlay",
      "atlas": "minecraft:blocks",
      "texture": "minecraft:block/nether_portal",
      "tint": 16777215
    }
  ]
}
```

This library is in beta, which means it is very prone to changes. For example, the custom portal block is meant only for the `SimplePortalBuilder` currently, but may be extended to JSON in the future to allow purely data-driven portals as well. A wiki will be made in the future explaining everything.