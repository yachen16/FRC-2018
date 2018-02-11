package frc.team4159.robot.commands.cube;

import edu.wpi.first.wpilibj.command.Command;
import frc.team4159.robot.Robot;
import frc.team4159.robot.subsystems.Superstructure;


public class LiftCube extends Command {

    public LiftCube() {
        requires(Superstructure.cubeHolder);
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void execute() {

        /* Controls intake/outtake */
        if(Robot.oi.intakeButton() && Robot.oi.outtakeButton()) {
            Superstructure.cubeHolder.stopFlywheels();

        } else if(Robot.oi.intakeButton()) {
            Superstructure.cubeHolder.intake();

        } else if(Robot.oi.outtakeButton()) {
            Superstructure.cubeHolder.outtake();

        } else {
            Superstructure.cubeHolder.stopFlywheels();
        }

        if(Robot.oi.openClaw()) {
            Superstructure.cubeHolder.open();
        } else {
            Superstructure.cubeHolder.close();
        }

        Superstructure.cubeHolder.updatePosition(Robot.oi.getSecondaryY());
        Superstructure.cubeHolder.move();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {

    }

    @Override
    protected void interrupted() {
        end();
    }
}