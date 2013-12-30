package resonantinduction.wire;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.input.Keyboard;

import resonantinduction.ResonantInduction;
import resonantinduction.TabRI;
import resonantinduction.Utility;
import resonantinduction.wire.flat.PartFlatWire;
import resonantinduction.wire.flat.RenderFlatWire;
import resonantinduction.wire.framed.PartWire;
import resonantinduction.wire.framed.RenderPartWire;
import universalelectricity.api.energy.UnitDisplay;
import universalelectricity.api.energy.UnitDisplay.Unit;
import calclavia.lib.Calclavia;
import calclavia.lib.render.EnumColor;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.ControlKeyModifer;
import codechicken.multipart.JItemMultiPart;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWire extends JItemMultiPart
{
    private Icon[] icons = new Icon[EnumWireMaterial.values().length];

    public ItemWire(int id)
    {
        super(ResonantInduction.CONFIGURATION.get(Configuration.CATEGORY_ITEM, "wire", id).getInt(id));
        this.setUnlocalizedName(ResonantInduction.PREFIX + "wire");
        this.setCreativeTab(TabRI.INSTANCE);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public TMultiPart newPart(ItemStack itemStack, EntityPlayer player, World world, BlockCoord pos, int side, Vector3 hit)
    {
        BlockCoord onPos = pos.copy().offset(side ^ 1);

        if (ControlKeyModifer.isControlDown(player))
        {
            PartWire wire = (PartWire) MultiPartRegistry.createPart("resonant_induction_wire", false);

            if (wire != null)
            {
                wire.preparePlacement(itemStack.getItemDamage());
            }

            return wire;
        }
        else
        {
            if (!Utility.canPlaceWireOnSide(world, onPos.x, onPos.y, onPos.z, ForgeDirection.getOrientation(side), false))
            {
                return null;
            }

            PartFlatWire wire = (PartFlatWire) MultiPartRegistry.createPart("resonant_induction_flat_wire", false);

            if (wire != null)
            {
                wire.preparePlacement(side, itemStack.getItemDamage());
            }

            return wire;
        }
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        return super.getUnlocalizedName(itemStack) + "." + EnumWireMaterial.values()[itemStack.getItemDamage()].getName().toLowerCase();
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4)
    {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
        {
            list.add("Hold " + EnumColor.AQUA + "shift" + EnumColor.GREY + " for more information");
        }
        else
        {
            list.add(EnumColor.AQUA + "Resistance: " + EnumColor.ORANGE + UnitDisplay.getDisplay(EnumWireMaterial.values()[itemstack.getItemDamage()].resistance, Unit.RESISTANCE));
            list.add(EnumColor.AQUA + "Current Capacity: " + EnumColor.ORANGE + UnitDisplay.getDisplay(EnumWireMaterial.values()[itemstack.getItemDamage()].maxAmps, Unit.AMPERE));
            list.add(EnumColor.AQUA + "Shock Damage: " + EnumColor.ORANGE + EnumWireMaterial.values()[itemstack.getItemDamage()].damage);
            list.addAll(Calclavia.splitStringPerWord("The energy transfer rate can be increased and the energy loss may be reduced by using a higher the voltage.", 5));
        }
    }

    @SideOnly(Side.CLIENT)
    public int getSpriteNumber()
    {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister register)
    {
        for (EnumWireMaterial material : EnumWireMaterial.values())
        {
            icons[material.ordinal()] = register.registerIcon(ResonantInduction.PREFIX + "wire." + EnumWireMaterial.values()[material.ordinal()].getName().toLowerCase());
        }

        RenderFlatWire.flatWireTexture = register.registerIcon(ResonantInduction.PREFIX + "models/flatWire");
        RenderPartWire.wireIcon = register.registerIcon(ResonantInduction.PREFIX + "models/wire");
        RenderPartWire.insulationIcon = register.registerIcon(ResonantInduction.PREFIX + "models/insulation" + (ResonantInduction.LO_FI_INSULATION ? "tiny" : ""));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage(int meta)
    {
        return icons[meta];
    }

    @Override
    public void getSubItems(int itemID, CreativeTabs tab, List listToAddTo)
    {
        for (EnumWireMaterial mat : EnumWireMaterial.values())
        {
            listToAddTo.add(new ItemStack(itemID, 1, mat.ordinal()));
        }
    }
}