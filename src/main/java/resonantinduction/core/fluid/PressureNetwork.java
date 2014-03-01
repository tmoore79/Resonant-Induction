package resonantinduction.core.fluid;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidHandler;
import resonantinduction.api.mechanical.fluid.IPressure;
import universalelectricity.api.net.IUpdate;
import universalelectricity.core.net.NetworkTickHandler;
import universalelectricity.core.net.NodeNetwork;

/**
 * The network for pipe fluid transfer. getNodes() is NOT used.
 * 
 * @author Calclavia
 */
public class PressureNetwork extends NodeNetwork<PressureNetwork, IPressurizedNode, IFluidHandler> implements IUpdate
{
	public PressureNetwork()
	{
		super(IPressurizedNode.class);
	}

	@Override
	public void update()
	{
		for (IPressurizedNode connector : getConnectors())
		{
			calculatePressure((IPressurizedNode) connector);
			distribute((IPressurizedNode) connector);
		}
	}

	@Override
	public boolean canUpdate()
	{
		return getConnectors().size() > 0;
	}

	@Override
	public boolean continueUpdate()
	{
		return canUpdate();
	}

	/**
	 * Calculate pressure in this pipe.
	 */
	public void calculatePressure(IPressurizedNode sourcePipe)
	{
		int totalPressure = 0;
		int findCount = 0;
		int minPressure = 0;
		int maxPressure = 0;

		Object[] connections = sourcePipe.getConnections();

		if (connections != null)
		{
			for (int i = 0; i < connections.length; i++)
			{
				Object obj = connections[i];

				if (obj instanceof IPressure)
				{
					int pressure = ((IPressure) obj).getPressure(ForgeDirection.getOrientation(i).getOpposite());

					minPressure = Math.min(pressure, minPressure);
					maxPressure = Math.max(pressure, maxPressure);
					totalPressure += pressure;
					findCount++;
				}
			}
		}

		if (findCount == 0)
		{
			sourcePipe.setPressure(0);
		}
		else
		{
			/**
			 * Create pressure loss.
			 */
			if (minPressure < 0)
				minPressure += 1;
			if (maxPressure > 0)
				maxPressure -= 1;

			sourcePipe.setPressure(Math.max(minPressure, Math.min(maxPressure, totalPressure / findCount + Integer.signum(totalPressure))));
		}
	}

	/**
	 * Distribute fluid in this pipe based on pressure.
	 */
	public void distribute(IPressurizedNode sourcePipe)
	{
		Object[] connections = sourcePipe.getConnections();

		for (int i = 0; i < connections.length; i++)
		{
			Object obj = connections[i];

			if (obj instanceof IPressurizedNode)
			{
				IPressurizedNode otherPipe = (IPressurizedNode) obj;

				/**
				 * Move fluid from higher pressure to lower. In this case, move from tankA to tankB.
				 */
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				int pressureA = sourcePipe.getPressure(dir);
				int pressureB = otherPipe.getPressure(dir.getOpposite());

				if (pressureA >= pressureB)
				{
					FluidTank tankA = sourcePipe.getInternalTank();
					FluidStack fluidA = tankA.getFluid();

					if (tankA != null && fluidA != null)
					{
						int amountA = fluidA.amount;

						if (amountA > 0)
						{
							FluidTank tankB = otherPipe.getInternalTank();

							if (tankB != null)
							{
								int amountB = tankB.getFluidAmount();

								int quantity = Math.max(pressureA > pressureB ? (pressureA - pressureB) * sourcePipe.getMaxFlowRate() : 0, Math.min((amountA - amountB) / 2, sourcePipe.getMaxFlowRate()));
								quantity = Math.min(Math.min(quantity, tankB.getCapacity() - amountB), amountA);

								if (quantity > 0)
								{
									FluidStack drainStack = sourcePipe.drain(dir.getOpposite(), quantity, false);

									if (drainStack != null && drainStack.amount > 0)
										sourcePipe.drain(dir.getOpposite(), otherPipe.fill(dir, drainStack, true), true);
								}
							}
						}
					}
				}
			}
			else if (obj instanceof IFluidHandler)
			{
				IFluidHandler fluidHandler = (IFluidHandler) obj;
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				int pressure = sourcePipe.getPressure(dir);
				int tankPressure = fluidHandler instanceof IPressure ? ((IPressure) fluidHandler).getPressure(dir.getOpposite()) : 0;
				FluidTank sourceTank = sourcePipe.getInternalTank();

				int transferAmount = (Math.max(pressure, tankPressure) - Math.min(pressure, tankPressure)) * sourcePipe.getMaxFlowRate();

				if (pressure > tankPressure)
				{
					if (sourceTank.getFluidAmount() > 0 && transferAmount > 0)
					{
						FluidStack drainStack = sourcePipe.drain(dir.getOpposite(), transferAmount, false);
						sourcePipe.drain(dir.getOpposite(), fluidHandler.fill(dir.getOpposite(), drainStack, true), true);
					}
				}
				else if (pressure < tankPressure)
				{
					if (transferAmount > 0)
					{
						FluidStack drainStack = fluidHandler.drain(dir.getOpposite(), transferAmount, false);

						if (drainStack != null)
						{
							fluidHandler.drain(dir.getOpposite(), sourcePipe.fill(dir.getOpposite(), drainStack, true), true);
						}
					}
				}
			}
		}
	}

	@Override
	public Class getConnectorClass()
	{
		return IPressurizedNode.class;
	}

	@Override
	public PressureNetwork newInstance()
	{
		return new PressureNetwork();
	}

	@Override
	public void reconstructConnector(IPressurizedNode connector)
	{
		connector.setNetwork(this);
	}

	@Override
	public void reconstruct()
	{
		NetworkTickHandler.addNetwork(this);
		super.reconstruct();
	}
}
