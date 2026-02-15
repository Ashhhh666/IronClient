package me.ash.odin.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.ash.odin.features.impl.iron.BreakerHelper;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(method = "startDestroyBlock", at = @At("HEAD"))
    private void onBlockHit(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        BreakerHelper.onHitBlock(pos);
    }
}