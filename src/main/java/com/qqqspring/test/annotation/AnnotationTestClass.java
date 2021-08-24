package com.qqqspring.test.annotation;

import javax.swing.tree.TreeNode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Johnson
 * 2021/7/26
 */
public class AnnotationTestClass {

    private Date estimatedStartTime;

    public Date getEstimatedStartTime() {
        return estimatedStartTime;
    }

    public void setEstimatedStartTime(Date estimatedStartTime) {
        this.estimatedStartTime = estimatedStartTime;
    }

}
