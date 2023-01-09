package com.kagg886.seiko.plugin.api;

import android.content.Context;
import com.kagg886.seiko.plugin.SeikoDescription;

/**
 * @projectName: Seiko
 * @package: com.kagg886.seiko.plugin.api
 * @className: SeikoPluginLoader
 * @author: kagg886
 * @description: APP实现此类，当Bot上线，下线，加载时调用此方法
 * @date: 2022/12/22 14:56
 * @version: 1.0
 */
public interface SeikoPlugin {
    void onBotGoLine(long botQQ);
    void onBotOffLine(long botQQ);

    void onLoad(Context context);
    SeikoDescription getDescription();
}
