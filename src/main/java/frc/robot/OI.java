/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.POVButton;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.robot.commands.climb.RunWinches;
import frc.robot.commands.climb.ToggleFrontFeet;
import frc.robot.commands.climb.feet.ToggleRearFeet;
import frc.robot.commands.spade.EjectHatch;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.SpadeHatcher;

import java.util.HashMap;

public class OI {

    private static OI INSTANCE = new OI();
    private Joystick leftFlight;
    private Joystick rightFlight;
    private XboxController xbox;

    private JoystickButton ejectHatch;
    private JoystickButton frontFeet;
    private JoystickButton rearFeet;

    private JoystickButton winchClimber;
    private POVButton winchReverse;

    private POVButton toggleAntiTip;
    private static HashMap<Integer, Double> armPositions = new HashMap<>();

    private OI() {
        leftFlight = new Joystick(RobotMap.OI.LEFT_JOY);
        rightFlight = new Joystick(RobotMap.OI.RIGHT_JOY);
        xbox = new XboxController(RobotMap.OI.XBOX);
        OI.initArmPositions();

        // button board binds
        /*for (Map.Entry<Integer, Double> e : OI.armPositions.entrySet()) {
            JoystickButton b = new JoystickButton(bb, e.getKey());
            b.whenPressed(new GotoPosition(e.getValue()));
            this.boardButtons[e.getKey()] = b;
        }*/

        frontFeet = new JoystickButton(xbox, RobotMap.OI.SECOND_LEVEL_CLIMBER);
        rearFeet = new JoystickButton(xbox, RobotMap.OI.REAR_FEET);
        winchClimber = new JoystickButton(xbox, RobotMap.OI.WINCH_CLIMBER);
        winchReverse = new POVButton(xbox, 180);
        toggleAntiTip = new POVButton(xbox, 0);

        // Xbox binds
        frontFeet.whenPressed(new ToggleFrontFeet());

        rearFeet.whenPressed(new ToggleRearFeet());
        rearFeet.whenReleased(new ToggleRearFeet());

        toggleAntiTip.whenPressed(new InstantCommand(() -> Drivetrain.getInstance().toggleAntiTip()));

        winchClimber.whenPressed(new RunWinches(RobotMap.CLIMBER.WINCH_POWER));
        winchClimber.whenReleased(new RunWinches(0));

        winchReverse.whenPressed(new RunWinches(-0.05));
        winchReverse.whenReleased(new RunWinches(0));

        ejectHatch = new JoystickButton(xbox, RobotMap.OI.EJECT_HATCH);

        ejectHatch.whenPressed(new EjectHatch());
        Runnable releaseEject = () -> {
            SpadeHatcher.getInstance().setEars(DoubleSolenoid.Value.kForward);
            SpadeHatcher.getInstance().setEjectors(false);
        };
        ejectHatch.whenReleased(new InstantCommand(SpadeHatcher.getInstance(), releaseEject));
    }

    /**
     * Returns 0.0 if the given value is within the specified range around zero. The remaining range
     * between the deadband and 1.0 is scaled from 0.0 to 1.0.
     *
     * @param value    value to clip
     * @param deadband range around zero
     * @author WPI
     */
    public static double removeDeadband(double value, double deadband) {
        if (Math.abs(value) > deadband) {
            if (value > 0.0) {
                return (value - deadband) / (1.0 - deadband);
            } else {
                return (value + deadband) / (1.0 - deadband);
            }
        } else {
            return 0.0;
        }
    }

    public static double removeDeadband(double y) {
        return removeDeadband(y, 0.05);
    }

    public double getLeftY() {
        return -removeDeadband(leftFlight.getY());
    }

    public int getLeftPOV() {
        return leftFlight.getPOV();
    }

    public double getRightY() {
        return -removeDeadband(rightFlight.getY());
    }

    public double getXboxRightX() {
        return removeDeadband(xbox.getX(GenericHID.Hand.kRight), 0.3);
    }

    public boolean getXboxLeftBumper() {
        return this.xbox.getBumper(GenericHID.Hand.kLeft);
    }

    public boolean getXboxRightBumper() {
        return this.xbox.getBumper(GenericHID.Hand.kRight);
    }

    public double getXboxLeftTrigger() {
        return this.xbox.getTriggerAxis(GenericHID.Hand.kLeft);
    }

    public double getXboxRightTrigger() {
        return this.xbox.getTriggerAxis(GenericHID.Hand.kRight);
    }

    public boolean getXboxA() {
        return this.xbox.getAButton();
    }

    public boolean getXboxB() {
        return this.xbox.getBButton();
    }

    public boolean climberSafetyOff() {
        return this.xbox.getBumper(GenericHID.Hand.kLeft);
    }

    public boolean cruiseControlCancel() {
        return (this.getLeftY() != 0 || this.getRightY() != 0) ||
                (this.getXboxA() || this.getXboxB()) ||
                (this.getXboxLeftBumper() || this.getXboxRightBumper());
    }

    private static void initArmPositions() {
        OI.armPositions = new HashMap<>();
        OI.armPositions.put(1, 90.0);
        OI.armPositions.put(3, 50.0);
        OI.armPositions.put(6, 45.0);
        OI.armPositions.put(2, 0.0);
        OI.armPositions.put(9, -45.0);
        OI.armPositions.put(12, -90.0);
    }

    public static OI getInstance() {
        return INSTANCE;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
