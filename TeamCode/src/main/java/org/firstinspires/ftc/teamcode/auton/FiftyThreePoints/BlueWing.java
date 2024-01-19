package org.firstinspires.ftc.teamcode.auton.FiftyThreePoints;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.robotParts.OpenCVTrussIsLeft;
import org.firstinspires.ftc.teamcode.robotParts.newAutonMethods;
import org.firstinspires.ftc.teamcode.robotParts.PixelManipulation;

@Autonomous(name = "BlueWing")
public class BlueWing extends LinearOpMode {
    newAutonMethods methods = new newAutonMethods(this);
    OpenCVTrussIsLeft camera = new OpenCVTrussIsLeft(this);
    PixelManipulation slides = new PixelManipulation(this);

    public void runOpMode() {
        methods.init(hardwareMap);
        slides.init(hardwareMap, telemetry);
        methods.calibrateEncoders();

        camera.findScoringPosition();
        // kijk of je assen kloppen de XYZ van imu, anders doet ie niks
        waitForStart();
        if (opModeIsActive()) {
            int finalPos = camera.pos;
            telemetry.addData("localPos", camera.pos);
            if (finalPos == 0) {
                methods.driveY(-90);
                methods.rotateToHeading(90);
                methods.driveX(-30);
                methods.driveY(-25);
                methods.driveY(40);

            } else if (finalPos == 1) {
                methods.driveY(-122);
                methods.driveY(20);

            } else if (finalPos == 2){
                methods.driveY(-16);
                methods.driveX(-40);
                methods.driveY(-90);
                methods.driveY(30);

            }
            sleep(30000);

        }
    }
}