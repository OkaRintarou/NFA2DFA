# NFA2DFA

## 功能

将NFA转换为DFA，NFA可以包含有空串的转换函数。

待转换的NFA需要以xml文档形式输入，转换结果会以xml文档输出。

## 使用方法

已提供图形界面。

## xml文档格式

以下为一个示例，实际应用若无需包含的标签可以省略

`<nodes>`根节点

`charSet`输入集

`node`状态节点

> 以下为包含在node中的节点

`name`状态名

`type`状态类型 

> 有START，END，NORMAL，START_END四种

`e`空串转换函数的目标状态，该节点只能有一个

`change`不含空串的转换函数，该节点可以有多个

> 以下为change中的节点

`charSet`输入

`next`目标状态

> 这两个节点成对出现，且一个change中只能有一对

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<nodes>
    <charSet>+-,0,.</charSet>
    <node>
        <name>q0</name>
        <type>START</type>
        <change>
            <charSet>+-</charSet>
            <next>q1</next>
        </change>
        <e>q1</e>
    </node>
    <node>
        <name>q1</name>
        <type>NORMAL</type>
        <change>
            <charSet>0</charSet>
            <next>q1,q4</next>
        </change>
        <change>
            <charSet>.</charSet>
            <next>q2</next>
        </change>
    </node>
    <node>
        <name>q2</name>
        <type>NORMAL</type>
        <change>
            <charSet>0</charSet>
            <next>q3</next>
        </change>
    </node>
    <node>
        <name>q3</name>
        <type>NORMAL</type>
        <change>
            <charSet>0</charSet>
            <next>q3</next>
        </change>
        <e>q5</e>
    </node>
    <node>
        <name>q4</name>
        <type>NORMAL</type>
        <change>
            <charSet>.</charSet>
            <next>q3</next>
        </change>
    </node>
    <node>
        <name>q5</name>
        <type>END</type>
    </node>
</nodes>
```

## 附加内容

已提供三个测试用例

- `NFA.xml`不含空串转换函数
- `NFAE.xml`包含空串转换函数
- `E.xml`用来测试空闭包

