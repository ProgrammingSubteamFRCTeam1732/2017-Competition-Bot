package org.usfirst.frc.team1732.robot.commands.placegear;

import java.util.function.DoubleSupplier;

import org.usfirst.frc.team1732.robot.Robot;
import org.usfirst.frc.team1732.robot.commands.drivetrain.BrakeDriveNoShift;
import org.usfirst.frc.team1732.robot.commands.drivetrain.encoder.DriveEncodersGetSetpointAtRuntime;
import org.usfirst.frc.team1732.robot.commands.drivetrain.encoder.ResetEncoderPID;
import org.usfirst.frc.team1732.robot.commands.drivetrain.encoder.SetEncoderPID;
import org.usfirst.frc.team1732.robot.commands.gearIntake.base.motor.GearIntakeOutTime;
import org.usfirst.frc.team1732.robot.commands.gearIntake.base.motor.GearIntakeSetIn;
import org.usfirst.frc.team1732.robot.commands.gearIntake.base.motor.GearIntakeSetStop;
import org.usfirst.frc.team1732.robot.commands.gearIntake.base.position.GearIntakeSetDown;
import org.usfirst.frc.team1732.robot.commands.gearIntake.base.position.GearIntakeSetUp;
import org.usfirst.frc.team1732.robot.commands.helpercommands.Wait;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class EncoderPlaceGear extends CommandGroup {

    public EncoderPlaceGear(double driveForwardDistance, double driveBackDistance) {
	this(driveForwardDistance, driveBackDistance, driveBackDistance);
    }

    public EncoderPlaceGear(double driveForwardDistance, double leftDriveBackDistance, double rightDriveBackDistance) {
	this(() -> driveForwardDistance, () -> leftDriveBackDistance, () -> rightDriveBackDistance);
    }

    public EncoderPlaceGear(DoubleSupplier driveForwardDistance, DoubleSupplier driveBackDistance) {
	this(driveForwardDistance, driveBackDistance, driveBackDistance);
    }

    public EncoderPlaceGear(DoubleSupplier driveForwardDistance, DoubleSupplier leftDriveBackDistance,
	    DoubleSupplier rightDriveBackDistance) {
	// drive into gear peg
	// addSequential(new TurnWithVision(0));
	addSequential(new SetEncoderPID(0.1, 0, 0));
	addParallel(new CommandGroup() {
	    {
		addSequential(new GearIntakeSetIn());
		addSequential(new Wait(0.5));
		addSequential(new GearIntakeSetStop());
	    }
	});
	addSequential(new DriveEncodersGetSetpointAtRuntime(driveForwardDistance));
	// addSequential(new DriveEncodersStraightRamp(driveForwardDistance,
	// driveForwardDistance));

	// place gear, drive back at same time
	addSequential(new Wait(0.2));
	addSequential(new GearIntakeSetDown());
	addParallel(new CommandGroup() {
	    {
		addSequential(new GearIntakeOutTime(1));
		addSequential(new Wait(0.5));
		addSequential(new GearIntakeSetUp());
	    }
	});
	addSequential(new BrakeDriveNoShift());
	addSequential(new SetEncoderPID(.05, 0, 0));
	addSequential(new DriveEncodersGetSetpointAtRuntime(leftDriveBackDistance, rightDriveBackDistance));
	addSequential(new ResetEncoderPID());
    }

    @Override
    public void interrupted() {
	end();
    }

    @Override
    public void end() {
	Robot.driveTrain.resetEncoderPID();
    }
}
