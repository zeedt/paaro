package com.plethub.paaro.webapp.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Paged {

    public static PageRequest getPageable(DataTablesInput input) {
        List<Sort.Order> orders = new ArrayList();
        Iterator var2 = input.getOrder().iterator();

        while(var2.hasNext()) {
            org.springframework.data.jpa.datatables.mapping.Order order = (org.springframework.data.jpa.datatables.mapping.Order)var2.next();
            Column column = (Column)input.getColumns().get(order.getColumn());
            if (column.getOrderable()) {
                String sortColumn = column.getData();
                Sort.Direction sortDirection = Sort.Direction.fromString(order.getDir());
                orders.add(new Sort.Order(sortDirection, sortColumn));
            }
        }

        Sort sort = orders.isEmpty() ? null : new Sort(orders);
        if (input.getLength() == -1) {
            input.setStart(0);
            input.setLength(2147483647);
        }

        return new PageRequest(input.getStart(), input.getLength(), sort);
    }
}
