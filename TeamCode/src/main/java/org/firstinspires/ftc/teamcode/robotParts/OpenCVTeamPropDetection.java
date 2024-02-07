package org.firstinspires.ftc.teamcode.robotParts;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

public class OpenCVTeamPropDetection {
    LinearOpMode myOpMode;
    OpenCvWebcam webcam1 = null;

    public int pos = 1; //Left 0, Middle 1, Right 2
    double leftAvgFin;
    double rightAvgFin;

    public OpenCVTeamPropDetection(LinearOpMode opMode) {myOpMode = opMode;}
    public void findScoringPosition(boolean IsTrussRight) {
        WebcamName webcamName = myOpMode.hardwareMap.get(WebcamName.class, "Webcam 1");
        int cameraMonitorViewId = myOpMode.hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", myOpMode.hardwareMap.appContext.getPackageName());
        webcam1 = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraMonitorViewId);

        webcam1.setPipeline(new brightnessPipeline(IsTrussRight));
        webcam1.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                webcam1.startStreaming(1280,720, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
    }
    class brightnessPipeline extends OpenCvPipeline {
        boolean TrussIsRight;
        brightnessPipeline(boolean IsTrussRight){TrussIsRight = IsTrussRight;}
        Mat YCbCr = new Mat();
        Rect trussLeftMidRect = new Rect(300,255, 180, 199);
        Rect trussLeftRightRect = new Rect(820, 260, 230, 240);
        Rect trussRightMidRect = new Rect(650,370, 180, 199);
        Rect trussRightLeftRect = new Rect(0, 380, 220, 260);

        Mat outPut = new Mat();
        Scalar redColor = new Scalar(255.0, 0.0, 0.0);
        Scalar greenColor = new Scalar(0.0, 255.0, 0.0);
        public Mat processFrame(Mat input) {
            Imgproc.cvtColor(input, YCbCr, Imgproc.COLOR_RGB2HSV);

            input.copyTo(outPut);
            Mat leftCrop;
            Mat rightCrop;
            if (TrussIsRight) {
                Imgproc.rectangle(outPut, trussRightLeftRect, redColor, 2);
                Imgproc.rectangle(outPut, trussRightMidRect, redColor, 2);
                leftCrop = YCbCr.submat(trussRightLeftRect);
                rightCrop = YCbCr.submat(trussRightMidRect);
            } else {
                Imgproc.rectangle(outPut, trussLeftMidRect, redColor, 2);
                Imgproc.rectangle(outPut, trussLeftRightRect, redColor, 2);
                leftCrop = YCbCr.submat(trussLeftMidRect);
                rightCrop = YCbCr.submat(trussLeftRightRect);
            }

            // For HSV: measures intensity so always use coi = 1. No clue what the other values do/measure/are.
            //For YCbCr: Blue = 1, Red = 2
            Core.extractChannel(leftCrop, leftCrop, 1);
            Core.extractChannel(rightCrop, rightCrop, 1);

            Scalar leftAvg = Core.mean(leftCrop);
            Scalar rightAvg = Core.mean(rightCrop);

            leftAvgFin = leftAvg.val[0];
            rightAvgFin = rightAvg.val[0];

            myOpMode.telemetry.addData("valueLeft", leftAvgFin);
            myOpMode.telemetry.addData("valueRight", rightAvgFin);

            if (TrussIsRight) {
                if (Math.abs(leftAvgFin - rightAvgFin) < 20) {
                    pos = 2;
                } else if (rightAvgFin > leftAvgFin) {
                    Imgproc.rectangle(outPut, trussRightMidRect, greenColor, 2);
                    pos = 1;
                } else if (leftAvgFin > rightAvgFin) {
                    Imgproc.rectangle(outPut, trussRightLeftRect, greenColor, 2);
                    pos = 0;
                }
            } else {
                if (Math.abs(leftAvgFin - rightAvgFin) < 20) {
                    pos = 0;
                } else if (rightAvgFin > leftAvgFin) {
                    Imgproc.rectangle(outPut, trussLeftRightRect, greenColor, 2);
                    pos = 2;
                } else {
                    Imgproc.rectangle(outPut, trussLeftMidRect, greenColor, 2);
                    pos = 1;
                }
            }

                if (pos == 0) {
                    myOpMode.telemetry.addData("Conclusion", "left");
                } else if (pos == 1) {
                    myOpMode.telemetry.addData("Conclusion", "mid");
                } else {
                    myOpMode.telemetry.addData("Conclusion", "right");
                }
                myOpMode.telemetry.addData("pos", pos);
                myOpMode.telemetry.update();
                return (outPut);
            }
    }
}