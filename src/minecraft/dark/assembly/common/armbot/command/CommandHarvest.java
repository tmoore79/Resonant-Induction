package dark.assembly.common.armbot.command;

import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import dark.api.al.coding.IArmbot;
import dark.api.al.coding.IProgramableMachine;
import dark.api.al.coding.IProcessTask.TaskType;

/** Used by arms to break a specific block in a position.
 *
 * @author Calclavia */
public class CommandHarvest extends CommandBreak
{

    public CommandHarvest()
    {
        super("Harvest");
    }

    @Override
    public ProcessReturn onMethodCalled(World world, Vector3 location, IProgramableMachine armbot)
    {
        this.keep = true;
        return super.onMethodCalled(world, location, armbot);
    }
}
