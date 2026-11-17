plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT" apply false
    // id("me.modmuss50.mod-publish-plugin") version "1.0.+" apply false
}

stonecutter active "1.21.11"

/*
// Make newer versions be published last
stonecutter tasks {
    order("publishModrinth")
    order("publishCurseforge")
}
 */

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    swaps["mod_version"] = "\"${properties["mod.version"]}\";"
    swaps["minecraft"] = "\"${current.version}\";"
    constants["release"] = properties["mod.id"] != "template"
    targets["fapi"] = properties["deps.fabric_api"]

    swaps["map_codec_swap"] = when {
        eval(current.version, "<1.20.5") -> "public static final com.mojang.serialization.Codec<$1> CODEC = RecordCodecBuilder.create(instance -> instance.group("
        else -> "public static final com.mojang.serialization.MapCodec<$1> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group("
    }

    replacements.string {
        direction = eval(current.version, ">=26.2")
        replace("net.minecraft.advancements.criterion.EntityPredicate", "net.minecraft.advancements.predicates.entity.EntityPredicate")
        replace("net.minecraft.advancements.criterion.ContextAwarePredicate", "net.minecraft.advancements.predicates.ContextAwarePredicate")
        replace("net.minecraft.advancements.criterion.ItemPredicate", "net.minecraft.advancements.predicates.ItemPredicate")
        replace("net.minecraft.advancements.criterion.NbtPredicate", "net.minecraft.advancements.predicates.NbtPredicate")
        replace("net.minecraft.advancements.criterion.StatePropertiesPredicate", "net.minecraft.advancements.predicates.StatePropertiesPredicate")
    }

    replacements.string {
        direction = eval(current.version, ">=26.1")
        replace("classTweaker v1 named", "classTweaker v1 official")
    }

    replacements.string {
        direction = eval(current.version, ">=1.21.11")
        replace("ResourceLocation", "Identifier")
        replace("net.minecraft.advancements.critereon", "net.minecraft.advancements.criterion")
        replace("net.minecraft.advancements.critereon.EntityPredicate", "net.minecraft.advancements.criterion.EntityPredicate")
        replace("net.minecraft.advancements.critereon.ContextAwarePredicate", "net.minecraft.advancements.criterion.ContextAwarePredicate")
        replace("net.minecraft.advancements.critereon.ItemPredicate", "net.minecraft.advancements.criterion.ItemPredicate")
        replace("net.minecraft.advancements.critereon.NbtPredicate", "net.minecraft.advancements.criterion.NbtPredicate")
        replace("net.minecraft.advancements.critereon.StatePropertiesPredicate", "net.minecraft.advancements.criterion.StatePropertiesPredicate")
    }

    replacements.string {
        id = "vec3f"
        direction = eval(current.version, ">=1.21.11")
        replace("Vector3f ", "Vector3fc ")
    }

    replacements.string {
        direction = eval(current.version, ">=1.21.3")
        replace("getMinBuildHeight", "getMinY")
        replace("getMaxBuildHeight", "getMaxY")
    }

    replacements.string {
        direction = eval(current.version, ">=1.20.6")
        replace("net.minecraft.world.level.chunk.ChunkStatus", "net.minecraft.world.level.chunk.status.ChunkStatus")
    }

    replacements.string {
        direction = eval(current.version, ">=1.20.4")
        replace("BlockBehaviour.Properties.copy", "BlockBehaviour.Properties.ofFullCopy")
    }
}
