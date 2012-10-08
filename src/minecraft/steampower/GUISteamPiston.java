package steampower;

import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.StatCollector;

import org.lwjgl.opengl.GL11;

import steampower.turbine.ContainerGenerator;
import steampower.turbine.TileEntitySteamPiston;
import universalelectricity.electricity.ElectricInfo;
import universalelectricity.electricity.ElectricInfo.ElectricUnit;

	public class GUISteamPiston extends GuiContainer
	{
	    private TileEntitySteamPiston tileEntity;

	    private int containerWidth;
	    private int containerHeight;

	    public GUISteamPiston(InventoryPlayer par1InventoryPlayer, TileEntitySteamPiston tileEntity)
	    {
	        super(new ContainerGenerator(par1InventoryPlayer, tileEntity));
	        this.tileEntity = tileEntity;
	    }

	    /**
	     * Draw the foreground layer for the GuiContainer (everything in front of the items)
	     */
	    protected void drawGuiContainerForegroundLayer()
	    {
	        this.fontRenderer.drawString("Steam Piston Guage Read outs", 55, 6, 4210752);
	        this.fontRenderer.drawString("MeterReadings", 90, 33, 4210752);
	        String displayText = "";
	        String displayText2 = "";
	        String displayText3 = "";
	        String displayText4 = "";
	        String displayText5 = "";
	        /**
	        if(tileEntity.connectedElectricUnit == null)
	        {
	        displayText = "Not Connected";
	        }
	        else*/
	        if(!tileEntity.running)
	        {
	        	if(tileEntity.steam> 0)
	        	{
	        	displayText = "No Load";
	        	}
	        	if(tileEntity.steam<= 0)
	        	{
	        	displayText = "No Steam";
	        	}
	        }
	        else
	        {
	        	//displayText = ElectricUnit.getWattDisplay((int)(tileEntity.generateRate*20));
	        	displayText =  "ForceOut: "+tileEntity.force+"N";
	        }
	        	displayText2 = "water" + "-" + tileEntity.water;
	        	displayText3 = "steam" + "-" + tileEntity.steam;
	        	
	        	displayText4 = "Db:PacketsReceived " + "=" + tileEntity.pCount;
	        	//displayText5 = "Debug:bforce" + "=" + tileEntity.bForce;
	        		
	        this.fontRenderer.drawString(displayText, (int)(105-displayText.length()*1), 45, 4210752);
	        this.fontRenderer.drawString(displayText2, (int)(105-displayText.length()*1), 55, 4210752);
	        this.fontRenderer.drawString(displayText3, (int)(105-displayText.length()*1), 65, 4210752);
	       this.fontRenderer.drawString(displayText4, (int)(105-displayText.length()*1), 75, 4210752);
	      // this.fontRenderer.drawString(displayText5, (int)(105-displayText.length()*1), 85, 4210752);
	        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
	    }

	    /**
	     * Draw the background layer for the GuiContainer (everything behind the items)
	     */
	    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	    {
	        int var4 = this.mc.renderEngine.getTexture(SteamPowerMain.textureFile+"SteamGUI.png");
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        this.mc.renderEngine.bindTexture(var4);
	        containerWidth = (this.width - this.xSize) / 2;
	        containerHeight = (this.height - this.ySize) / 2;
	        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	    }
	}
