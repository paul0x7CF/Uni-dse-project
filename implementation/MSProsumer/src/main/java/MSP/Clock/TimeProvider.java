package MSP.Clock;

import java.time.LocalTime;

public class TimeProvider implements ITimeProvider{

    @Override
    public LocalTime now() {
        return LocalTime.now();
    }
}
