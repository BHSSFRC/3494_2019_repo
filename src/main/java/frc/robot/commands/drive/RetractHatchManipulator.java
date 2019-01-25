package frc.robot.commands.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.HatchManipulator;


public class RetractHatchManipulator extends Command {
    public RetractHatchManipulator() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(HatchManipulator.getInstance());
    }


    @Override
    protected void initialize() {
        HatchManipulator.getInstance().retract();
    }


    @Override
    protected boolean isFinished() {
        // TODO: Make this return true when this Command no longer needs to run execute()
        return true;
    }


    @Override
    protected void interrupted() {
        super.interrupted();
    }
}
