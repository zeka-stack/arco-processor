package sample.b;

import dev.dong4j.zeka.processor.annotation.AutoListener;
import java.util.Map;
import org.springframework.core.env.ConfigurableEnvironment;
import dev.dong4j.zeka.starter.launcher.spi.LauncherInitiation;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2023.11.11 22:24
 * @since 2024.1.1
 */
@AutoListener
public class AutoListenerTest implements LauncherInitiation {

    /**
     * Launcher
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the map
     * @since 2024.1.1
     */
    @Override
    public Map<String, Object> launcher(ConfigurableEnvironment env, String appName, boolean isLocalLaunch) {
        return null;
    }

    /**
     * Gets name *
     *
     * @return the name
     * @since 2024.1.1
     */
    @Override
    public String getName() {
        return "xxx";
    }
}
