package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.robotParts.DrivetrainAlex;

@TeleOp
public class armHookTest extends LinearOpMode {
    DrivetrainAlex drivetrain = new DrivetrainAlex();

    @Override
    public void runOpMode() throws InterruptedException {

        drivetrain.init(hardwareMap);
        DcMotor hook = hardwareMap.dcMotor.get("hook");
        DcMotor intake = hardwareMap.dcMotor.get("intake");

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double x = gamepad1.left_stick_x; // y direction is reversed
            double y = -gamepad1.left_stick_y;
            double rotate = gamepad1.right_stick_x;

            double hookPower = gamepad1.right_trigger - gamepad1.left_trigger;

            DrivetrainAlex.maxSpeed = 1;

            if(gamepad1.left_bumper) {
                intake.setPower(1);
            } else if (gamepad1.right_bumper) {
                intake.setPower(-1);
            }
            else {
                intake.setPower(0);
            }

            hook.setPower(hookPower);

            drivetrain.drive(y, x, rotate);
        }
    }
}