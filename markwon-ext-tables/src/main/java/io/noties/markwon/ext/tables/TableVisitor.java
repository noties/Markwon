package io.noties.markwon.ext.tables;

import com.vladsch.flexmark.ext.tables.TableCell;
import com.vladsch.flexmark.ext.tables.TableHead;
import com.vladsch.flexmark.ext.tables.TableRow;
import com.vladsch.flexmark.util.ast.VisitHandler;

/**
 * <p>
 *
 * @author cpacm 2023/4/4
 */
public interface TableVisitor {

    void visit(TableCell node);

    void visit(TableHead head);

    void visit(TableRow row);

    public static <V extends TableVisitor> VisitHandler<?>[] VISIT_HANDLERS(V visitor) {
        return new VisitHandler<?>[]{
                new VisitHandler<>(TableCell.class, visitor::visit),
                new VisitHandler<>(TableHead.class, visitor::visit),
                new VisitHandler<>(TableRow.class, visitor::visit),
        };
    }
}
