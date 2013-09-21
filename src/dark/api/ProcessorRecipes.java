package dark.api;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import dark.core.prefab.helpers.AutoCraftingManager;
import dark.core.prefab.helpers.Pair;

/** Recipes for ore processor machines
 *
 * @author DarkGuardsman */
public class ProcessorRecipes
{
    private static Random random = new Random();

    public static enum ProcessorType
    {
        CRUSHER(),
        GRINDER(),
        PRESS();
        public HashMap<Pair<Integer, Integer>, ItemStack> recipes = new HashMap();
        public HashMap<Pair<Integer, Integer>, Pair<ItemStack, Float>> recipesChance = new HashMap();
        public HashMap<Pair<Integer, Integer>, Float> recipesChanceSalvage = new HashMap();
        public HashMap<Pair<Integer, Integer>, ItemStack> damagedOutput = new HashMap();

    }

    static
    {
        createRecipe(ProcessorType.CRUSHER, new ItemStack(Block.stone.blockID, 1, 0), new ItemStack(Block.cobblestone.blockID, 1, 0));
        createRecipe(ProcessorType.CRUSHER, new ItemStack(Block.oreDiamond.blockID, 1, 0), new ItemStack(Item.diamond.itemID, 1, 0));
        createRecipe(ProcessorType.CRUSHER, new ItemStack(Block.oreLapis.blockID, 1, 0), new ItemStack(Item.dyePowder.itemID, 4, ColorCode.BLUE.ordinal()));
        createRecipe(ProcessorType.CRUSHER, new ItemStack(Block.oreRedstone.blockID, 1, 0), new ItemStack(Item.redstone.itemID, 4, 0));
        createRecipe(ProcessorType.CRUSHER, new ItemStack(Block.oreEmerald.blockID, 1, 0), new ItemStack(Item.redstone.itemID, 4, 0));

        createRecipe(ProcessorType.GRINDER, new ItemStack(Block.cobblestone.blockID, 1, 0), new ItemStack(Block.sand.blockID, 1, 0));

        markOutputSalavageWithChance(ProcessorType.CRUSHER, new ItemStack(Block.chest, 1), .8f);
        markOutputSalavageWithChance(ProcessorType.CRUSHER, new ItemStack(Block.brick, 1), .7f);
    }

    /** Creates a simple one itemStack in one ItemStack out. Itemstack output can actual have a stack
     * size larger than one
     *
     * @param type - processor type
     * @param in - input item, stacksize is ignored
     * @param out - ouput item */
    public static void createRecipe(ProcessorType type, Object in, Object out)
    {
        if (in != null && out != null && type != null)
        {
            ItemStack input = convert(in);
            ItemStack output = convert(out);
            if (input != null && output != null)
            {
                HashMap<Pair<Integer, Integer>, ItemStack> map = type.recipes;
                if (map != null && !map.containsKey(input))
                {
                    map.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), output);
                }
            }
        }
    }

    /** Creates a recipe that has a chance of failing
     *
     * @param type - processor type
     * @param in - input item stack, stack size is ignored
     * @param out - output item stack, stack size is used
     * @param chance - chance to fail with 1 being zero chance and zero being 100% chance */
    public static void createRecipeWithChance(ProcessorType type, Object in, Object out, float chance)
    {
        if (in != null && out != null && type != null)
        {
            ItemStack input = convert(in);
            ItemStack output = convert(out);
            if (input != null && output != null)
            {
                HashMap<Pair<Integer, Integer>, Pair<ItemStack, Float>> map = type.recipesChance;
                if (map != null && !map.containsKey(input))
                {
                    map.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), new Pair<ItemStack, Float>(output, chance));
                }
            }
        }
    }

    /** Not so much of a recipe but it applies a change on the item. TODO improve and control actual
     * output of the recipe */
    public static void markOutputSalavageWithChance(ProcessorType type, Object in, float chance)
    {
        if (in != null && type != null)
        {
            ItemStack input = convert(in);
            if (input != null && input != null)
            {
                HashMap<Pair<Integer, Integer>, Float> map = type.recipesChanceSalvage;
                if (map != null && !map.containsKey(input))
                {
                    map.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), chance);
                }
            }
        }
    }

    /** Used to track items that should be converted to different items during salvaging. */
    public static void createSalvageDamageOutput(ProcessorType type, Object in, Object out)
    {
        if (in != null && out != null && type != null)
        {
            ItemStack input = convert(in);
            ItemStack output = convert(out);
            if (input != null && output != null)
            {
                HashMap<Pair<Integer, Integer>, ItemStack> map = type.damagedOutput;
                if (map != null && !map.containsKey(input))
                {
                    map.put(new Pair<Integer, Integer>(input.itemID, input.getItemDamage()), output);
                }
            }
        }
    }

    /** Converts an object input into an itemstack for use */
    private static ItemStack convert(Object object)
    {
        if (object instanceof ItemStack)
        {
            return (ItemStack) object;
        }
        if (object instanceof Block)
        {
            return new ItemStack(((Block) object).blockID, 1, -1);
        }
        if (object instanceof Item)
        {
            return new ItemStack(((Item) object).itemID, 1, -1);
        }
        return null;
    }

    /** Gets the lit of items that are created from the input item stack. General this will be an
     * array of one item. However, in salavaging cases it can be up to 8 items.
     *
     * @param type - Processor type
     * @param stack - item stack input ignores stacksize
     * @return array of itemStacks */
    public static ItemStack[] getOuput(ProcessorType type, ItemStack stack, boolean damageSalvage)
    {
        if (stack == null || type == null || stack.getItem() == null)
        {
            return null;
        }
        HashMap<Pair<Integer, Integer>, ItemStack> map = type.recipes;
        HashMap<Pair<Integer, Integer>, Pair<ItemStack, Float>> mapChance = type.recipesChance;
        HashMap<Pair<Integer, Integer>, Float> mapSalvage = type.recipesChanceSalvage;
        HashMap<Pair<Integer, Integer>, ItemStack> altSalvageMap = type.damagedOutput;
        Pair<Integer, Integer> blockSet = new Pair<Integer, Integer>(stack.itemID, stack.getItemDamage());
        if (map == null)
        {
            return null;
        }
        ItemStack re = map.get(new Pair<Integer, Integer>(stack.itemID, -1));
        if (re != null)
        {
            return new ItemStack[] { re };
        }
        re = map.get(blockSet);
        if (re != null)
        {
            return new ItemStack[] { re };
        }
        Pair<ItemStack, Float> ree = mapChance.get(blockSet);
        if (ree != null && random.nextFloat() >= ree.getValue())
        {
            return new ItemStack[] { ree.getKey() };
        }
        float chance = 0;
        try
        {
            chance = mapSalvage != null ? mapSalvage.get(blockSet) : 0;
        }
        catch (Exception e)
        {
        }
        if (chance == 0)
        {
            chance = .1f;
        }
        ItemStack[] recipeList = AutoCraftingManager.getReverseRecipe(stack.copy());
        ItemStack[] reList = null;
        if (recipeList != null)
        {
            reList = new ItemStack[recipeList.length];
            for (int i = 0; i < recipeList.length; i++)
            {
                if (recipeList[i] != null && random.nextFloat() >= chance)
                {
                    int meta = recipeList[i].getItemDamage();
                    NBTTagCompound tag = recipeList[i].getTagCompound();
                    if (recipeList[i].itemID < Block.blocksList.length && Block.blocksList[recipeList[i].itemID] != null && recipeList[i].getItemDamage() > 16)
                    {
                        meta = 0;

                    }
                    reList[i] = new ItemStack(recipeList[i].itemID, recipeList[i].stackSize, meta);
                    reList[i].setTagCompound(tag);
                    if (damageSalvage && altSalvageMap != null && altSalvageMap.containsKey(new Pair<Integer, Integer>(reList[i].itemID, reList[i].getItemDamage())))
                    {
                        reList[i] = altSalvageMap.get(new Pair<Integer, Integer>(reList[i].itemID, reList[i].getItemDamage()));
                    }
                }
            }
        }
        return reList;
    }

    public static void parseOreNames()
    {

    }
}
