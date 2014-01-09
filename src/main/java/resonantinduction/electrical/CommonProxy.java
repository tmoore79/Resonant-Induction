package resonantinduction.electrical;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import codechicken.lib.colour.ColourRGBA;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import resonantinduction.core.prefab.ProxyBase;
import resonantinduction.electrical.multimeter.ContainerMultimeter;
import resonantinduction.electrical.multimeter.PartMultimeter;
import universalelectricity.api.vector.Vector3;

public class CommonProxy extends ProxyBase
{
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TileMultipart)
		{
			TMultiPart part = ((TileMultipart) tileEntity).partMap(id);
			if (part instanceof PartMultimeter)
			{
				return new ContainerMultimeter(player.inventory, ((PartMultimeter) part));
			}
		}

		return null;
	}

	public void renderElectricShock(World world, Vector3 start, Vector3 target, float r, float g, float b, boolean split)
	{

	}

	public void renderElectricShock(World world, Vector3 start, Vector3 target, float r, float g, float b)
	{
		this.renderElectricShock(world, start, target, r, g, b, true);
	}

	public void renderElectricShock(World world, Vector3 start, Vector3 target, ColourRGBA color)
	{
		this.renderElectricShock(world, start, target, color.r / 255f, color.g / 255f, color.b / 255f);
	}

	public void renderElectricShock(World world, Vector3 start, Vector3 target, ColourRGBA color, boolean split)
	{
		this.renderElectricShock(world, start, target, color.r / 255f, color.g / 255f, color.b / 255f, split);
	}

	public void renderElectricShock(World world, Vector3 start, Vector3 target)
	{
		this.renderElectricShock(world, start, target, true);
	}

	public void renderElectricShock(World world, Vector3 start, Vector3 target, boolean b)
	{
		this.renderElectricShock(world, start, target, 0.55f, 0.7f, 1f, b);

	}
}