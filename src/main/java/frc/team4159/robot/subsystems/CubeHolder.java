package frc.team4159.robot.subsystems;


import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team4159.robot.commands.cube.LiftCube;

import static frc.team4159.robot.Constants.NOMINAL_OUT_PERCENT;
import static frc.team4159.robot.Constants.PEAK_OUT_PERCENT;
import static frc.team4159.robot.Constants.TIMEOUT_MS;
import static frc.team4159.robot.RobotMap.*;

public class CubeHolder extends Subsystem {

    private static CubeHolder instance;

    public static CubeHolder getInstance() {
        if(instance == null)
            instance = new CubeHolder();
        return instance;
    }

    private VictorSP leftVictor, rightVictor;
    private DoubleSolenoid pistons;
    private TalonSRX liftTalon;
    private final int PIDIDX = 0;
    private final int MAX_SPEED = 5; // encoder units per cycle TODO: Test and change as necessary
    private int targetPosition; // In encoder units. 4096 per revolution.
    private final double kF = 0.0;
    private final double kP = 0.1;
    private final double kI = 0.0;
    private final double kD = 0.0;

    private CubeHolder() {

        leftVictor = new VictorSP(LEFT_CUBE_VICTOR);
        rightVictor = new VictorSP(RIGHT_CUBE_VICTOR);
        liftTalon = new TalonSRX(LIFT_TALON);
        pistons = new DoubleSolenoid(FORWARD_CHANNEL, REVERSE_CHANNEL);

        targetPosition = 0; // Initial encoder value when lifter is down

        configureSensors();
    }

    private void configureSensors() {

        // TODO: Add a limit switch, hall effect, talon tech, etc. to zero out encoder

        final int SLOTIDX = 0;

        liftTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, PIDIDX, TIMEOUT_MS);
        liftTalon.setSensorPhase(false);
        liftTalon.configNominalOutputForward(NOMINAL_OUT_PERCENT, TIMEOUT_MS);
        liftTalon.configNominalOutputReverse(NOMINAL_OUT_PERCENT, TIMEOUT_MS);
        liftTalon.configPeakOutputForward(PEAK_OUT_PERCENT, TIMEOUT_MS);
        liftTalon.configPeakOutputReverse(-PEAK_OUT_PERCENT, TIMEOUT_MS);

        // TODO: Figure out allowable closed loop error units and value
        liftTalon.configAllowableClosedloopError(SLOTIDX, 0, TIMEOUT_MS);

        liftTalon.config_kF(SLOTIDX, kF, TIMEOUT_MS);
        liftTalon.config_kP(SLOTIDX, kP, TIMEOUT_MS);
        liftTalon.config_kI(SLOTIDX, kI, TIMEOUT_MS);
        liftTalon.config_kD(SLOTIDX, kD, TIMEOUT_MS);

    }

    /* Runs wheels inwards to intake the cube */
    public void intake() {
        leftVictor.set(-1);
        rightVictor.set(1);
    }

    /* Runs wheels outwards to outtake the cube */
    public void outtake() {
        leftVictor.set(1);
        rightVictor.set(-1);
    }

    /* Stops running the wheels */
    public void stopFlywheels() {
        leftVictor.set(0);
        rightVictor.set(0);
    }

    /* Opens the claw */
    public void open() {
        pistons.set(DoubleSolenoid.Value.kForward);
    }

    /* Closes the claw */
    public void close() {
        pistons.set(DoubleSolenoid.Value.kReverse);

    }

    public void setRawLift(double value) {
        liftTalon.set(ControlMode.PercentOutput, value);
    }

    public void move() {

//        if(limit switch triggered) {
//            liftTalon.setSelectedSensorPosition(0, PIDIDX, TIMEOUT_MS);
//            targetPosition = 0;
//        }

        if(targetPosition < 0)
            targetPosition = 0;
        if(targetPosition > 1050) // 90 degrees is 1024
            targetPosition = 1024;

        liftTalon.set(ControlMode.Position, targetPosition);
    }

    /* Updates target position to a value from -MAX_SPEED to +MAX_SPEED according to the joystick value */
    public void updatePosition(double value) {
        value *= MAX_SPEED;
        targetPosition += (int)value;
    }

    public void initDefaultCommand() {
        setDefaultCommand(new LiftCube());
    }
}