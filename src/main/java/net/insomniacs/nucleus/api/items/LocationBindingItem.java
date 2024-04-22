package net.insomniacs.nucleus.api.items;

import net.insomniacs.nucleus.api.components.custom.LocationBindingComponent;
import net.insomniacs.nucleus.api.components.NucleusComponents;
import net.insomniacs.nucleus.api.utils.Location;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class LocationBindingItem extends Item {

    public static float hasLocationPredicate(ItemStack stack) {
        return hasLocation(stack) ? 1 : 0;
    }

    public LocationBindingItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        var result = super.use(world, player, hand);
        if (result.getResult().isAccepted() || !player.isSneaking()) return result;

        ItemStack stack = player.getStackInHand(hand);
        if (!hasLocation(stack)) return result;

        stack.remove(NucleusComponents.BOUND_LOCATION);
        return TypedActionResult.success(stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getSide() == Direction.DOWN) return ActionResult.FAIL;

        ItemPlacementContext placement = new ItemPlacementContext(context);
        BlockPos pos = placement.getBlockPos();
        World world = context.getWorld();
        if (world.getBlockState(pos.down()).isAir()) return ActionResult.FAIL;

        ItemStack stack = context.getStack();
        RegistryKey<World> worldKey = world.getRegistryKey();
        Location location = new Location(pos, worldKey);
        if (getLocation(stack).equals(location)) return ActionResult.FAIL;

        stack.set(NucleusComponents.BOUND_LOCATION, LocationBindingComponent.simple(location));
        return ActionResult.SUCCESS;
    }

    public static boolean hasLocation(ItemStack stack) {
        return stack.contains(NucleusComponents.BOUND_LOCATION);
    }

    public static Location getLocation(ItemStack stack) {
        return stack.getOrDefault(NucleusComponents.BOUND_LOCATION, LocationBindingComponent.EMPTY).location();
    }

}
