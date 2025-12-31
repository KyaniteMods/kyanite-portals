plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT" apply false
    // id("me.modmuss50.mod-publish-plugin") version "1.0.+" apply false
}

stonecutter active "1.20.1"

/*
// Make newer versions be published last
stonecutter tasks {
    order("publishModrinth")
    order("publishCurseforge")
}
 */

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    swaps["mod_version"] = "\"${property("mod.version")}\";"
    swaps["minecraft"] = "\"${node.metadata.version}\";"
    constants["release"] = property("mod.id") != "template"
    dependencies["fapi"] = node.project.property("deps.fabric_api") as String

    swaps["map_codec_swap"] = when {
        eval(current.version, "<1.20.5") -> "public static final Codec<$1> CODEC = RecordCodecBuilder.create(instance -> instance.group("
        else -> "public static final MapCodec<$1> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group("
    }

    replacements {
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
            replace("net.minecraft.advancements.critereon", "net.minecraft.advancements.criterion")
        }
    }

    replacements {
        string("vec3f", current.parsed >= "1.21.11") {
            replace("Vector3f ", "Vector3fc ")
        }
    }

    replacements {
        string(current.parsed >= "1.21.3") {
            replace("getMinBuildHeight", "getMinY")
            replace("getMaxBuildHeight", "getMaxY")
        }
    }

    replacements {
        string(current.parsed >= "1.20.4") {
            replace("BlockBehaviour.Properties.copy", "BlockBehaviour.Properties.ofFullCopy")
        }
    }
}
