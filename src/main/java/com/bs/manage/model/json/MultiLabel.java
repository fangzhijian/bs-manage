package com.bs.manage.model.json;

import com.bs.manage.model.bean.common.LabelModel;
import com.bs.manage.until.NumberUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 2020/2/26 13:22
 * fzj
 */
@Data
@Accessors(chain = true)
public class MultiLabel {

    private Long id;                    //id
    private Long value;                 //id,这个字段之前php版本要用到
    private String label;               //标签说明
    private List<MultiLabel> children;  //下一层

    public static <T extends LabelModel> List<MultiLabel> getInstance(List<T> list) {
        List<MultiLabel> topLabelList = new ArrayList<>();
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (NumberUtil.isBlank(next.getParent_id())) {
                topLabelList.add(new MultiLabel().setId(next.getId()).setValue(next.getId()).setLabel(next.getName()));
                iterator.remove();
            }
        }
        setChild(topLabelList,list);
        return topLabelList;
    }

    public static <T extends LabelModel> void setChild(List<MultiLabel> topLabelList, List<T> list) {
        for (MultiLabel multiLabel : topLabelList) {
            List<MultiLabel> children = null;
            Iterator<T> iterator = list.iterator();
            while (iterator.hasNext()) {
                T next = iterator.next();
                if (multiLabel.getId().equals(next.getParent_id())) {
                    if (children == null){
                        children = new ArrayList<>();
                    }
                    children.add(new MultiLabel().setId(next.getId()).setValue(next.getId()).setLabel(next.getName()));
                    iterator.remove();
                }
            }
            multiLabel.setChildren(children);
            if (children != null && list.size() > 0) {
                setChild(children, list);
            }
        }

    }
}
