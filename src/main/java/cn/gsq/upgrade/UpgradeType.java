package cn.gsq.upgrade;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;

@Getter
@AllArgsConstructor
public enum UpgradeType {
    FILE(1, "file", "文件更新，直接替换文件"),

    CONFIG(2, "config", "配置更新，先获取配置，在改配置项"),

    SQL(3, "sql", "sql文件更新"),

    OTHERS(4, "others", "其他");

    private Integer code;   // 类型编号

    private String name;  // 类型名称

    private String describe;  // 描述

    private static HashMap<Integer, UpgradeType> examples = new HashMap<>();

    static {
        for(UpgradeType event : UpgradeType.values()){
            examples.put(event.getCode(), event);
        }
    }

    public static UpgradeType parse(Integer code){
        if(examples.containsKey(code)){
            return examples.get(code);
        }
        return null;
    }
}
