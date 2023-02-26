import java.util.*;
import java.lang.Math;
public class PoseTracking {
    public static boolean compare(ArrayList<Pose> base, ArrayList<Pose> tocompare, double threshold) {
        Pose first1 = base.get(0);
        Pose first2 = tocompare.get(0);
        if (base.size() > tocompare.size()) {
            return false;
        }
        Pose subtracted1;
        Pose subtracted2;
        double absoluteerror = 0;
        for (int i = 1; i < base.size(); i++) {
            subtracted1 = subtract(first1, base.get(i));
            subtracted2 = subtract(first2, tocompare.get(i));
            absoluteerror += Math.abs(subtracted1.roll-subtracted2.roll);
            absoluteerror += Math.abs(subtracted1.pitch-subtracted2.pitch);
            absoluteerror += Math.abs(subtracted1.yaw-subtracted2.yaw);
        }

        System.out.print("Total error: " + absoluteerror);
        
        if (absoluteerror < threshold) {
            System.out.println(" (passed)");
            return true;
        }
        System.out.println(" (failed)");
        return false;
    }

    public static Pose subtract(Pose base, Pose tosubtract) {
        return new Pose(base.roll-tosubtract.roll, base.pitch-tosubtract.pitch, base.yaw-tosubtract.yaw);
    }


    public static class Pose {
		double roll;
		double pitch;
		double yaw;

		public Pose(double _roll, double _pitch, double _yaw) {
			this.roll = _roll;
			this.pitch = _pitch;
			this.yaw = _yaw;
		}
	}
}
