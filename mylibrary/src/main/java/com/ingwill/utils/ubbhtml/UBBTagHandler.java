package com.ingwill.utils.ubbhtml;

public interface UBBTagHandler {

    /**
     * 解析一个可能的UBB标签（扩在[]之间的内容），如果是一个合法的UBB标签，则实现方法
     * 要负责把传入的potentialTag解析成标签符号(tag)和属性(attribute)两部分，并作为
     * 一个字符串数组传回，如果potentialTag并非一个合法的UBB标签，则应返回null。
     * 注意'['和']'是不会作为potentialTag的一部分传给本方法的。
     * @param potentialTag 
     * @param isEmpty 是否是一个空标签，即形式为[.../]，没有内层嵌套文本的UBB标签
     * @return 如果输入是合法UBB标签，则返回经解析后的的标签符号和相应属性，否则返回null
     */
    public String[] parseTag(String potentialTag, boolean isEmpty);

    /**
     * 把给定的UBB标签转换组合成输出形式
     * @param tag 标签符号
     * @param attr 标签属性
     * @param data 标签中的内容（已经转换成输出形式后的）
     * @param isEmpty 是否是一个空标签，即形式为[.../]，没有子标签的UBB标签
     * @return 组合好的字符串
     */
    public String compose(String tag, String[] attr, String data, boolean isEmpty);
    
}
