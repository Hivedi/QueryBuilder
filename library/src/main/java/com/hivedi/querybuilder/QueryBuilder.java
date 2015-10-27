package com.hivedi.querybuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

public class QueryBuilder {

    private ArrayList<String> select = new ArrayList<String>();
    private ArrayList<String> tableNames = new ArrayList<String>();
    private ArrayList<String> where = new ArrayList<String>();
    private ArrayList<String> join = new ArrayList<String>();
    private ArrayList<String> like = new ArrayList<String>();
    private ArrayList<String> likeOr = new ArrayList<String>();
    private ArrayList<String> group = new ArrayList<String>();
    private ArrayList<String> order = new ArrayList<String>();
    private ArrayList<String> into = new ArrayList<String>();
    private ArrayList<String> values = new ArrayList<String>();
    private LinkedHashMap<String, String> update = new LinkedHashMap<>();
    private String limit = "";
    private Boolean distinct = null;

    private ArrayList<String> params = new ArrayList<String>();

    /**
     * get params to query bind
     * @return String[] <br>or <br>null
     */
    public String[] getParams(){
        return params.size() > 0 ? (String [])params.toArray(new String[params.size()]) : null;
    }

    public QueryBuilder addParam(String param) {
        if (param != null && !param.equals(""))
            params.add(param);
        return this;
    }

    public QueryBuilder addParam(Integer param) {
        if (param != null)
            params.add(param.toString());
        return this;
    }

    public QueryBuilder addParam(Long param) {
        if (param != null)
            params.add(param.toString());
        return this;
    }

    public QueryBuilder addParam(Double param) {
        if (param != null)
            params.add(param.toString());
        return this;
    }

    public QueryBuilder addParams(String[] param) {
        if (param != null && param.length > 0) {
            Collections.addAll(this.params, param);
        }
        return this;
    }

    public QueryBuilder addParams(ArrayList<String> param) {
        for(String p : param) {
            this.params.add(p);
        }
        return this;
    }

    public QueryBuilder select(String selectCols) {
        this.select.add(selectCols);
        return this;
    }

    public QueryBuilder into(String selectCols) {
        this.into.add(selectCols);
        return this;
    }

    public QueryBuilder values(String vals) {
        this.values.add(vals);
        return this;
    }

    public QueryBuilder from(String tableName) {
        this.tableNames.add(tableName);
        return this;
    }

    public QueryBuilder where(String whereQuery) {
        if (whereQuery != null && !whereQuery.equals("")) {
            this.where.add(whereQuery);
        }
        return this;
    }

    /**
     *
     * @param filed - db column name
     * @param compaction - sign = > < <>
     * @param value - value to params
     * @return self
     */
    public QueryBuilder where(String filed, String compaction, String value) {
        //if (whereQuery != null && !whereQuery.equals(""))
        //	this.where.add(whereQuery);
        this.where.add(filed + compaction + "?");
        addParam(value);
        return this;
    }

    public QueryBuilder limit(Integer limit) {
        this.limit = Integer.toString(limit);
        return this;
    }

    public QueryBuilder limit(Integer limit, Integer offset) {
        this.limit = Integer.toString(limit) + "," + Integer.toString(offset);
        return this;
    }

    public QueryBuilder distinct(Boolean useDistinct) {
        this.distinct = useDistinct;
        return this;
    }

    public QueryBuilder join(String joinTable, String joinON, JoinType typeJoin) {
        this.join.add(" " + typeJoin.asString() + " JOIN " + joinTable + " ON (" + joinON + ") ");
        return this;
    }

    public QueryBuilder join(String allJoin) {
        this.join.add(" " + allJoin + " ");
        return this;
    }

    public QueryBuilder like(String likeQuery) {
        this.like.add(likeQuery);
        return this;
    }

    public QueryBuilder likeOr(String likeQuery) {
        this.likeOr.add(likeQuery);
        return this;
    }

    public QueryBuilder groupBy(String groupQuery) {
        this.group.add(groupQuery);
        return this;
    }

    public QueryBuilder orderBy(String orderQuery) {
        this.order.add(orderQuery);
        return this;
    }

    public QueryBuilder updateSet(String field, String value) {
        this.update.put(field, value);
        return this;
    }

    /**
     * clear all aprams
     */
    public void clear(){
        this.select.clear();
        this.tableNames.clear();
        this.where.clear();
        this.join.clear();
        this.like.clear();
        this.likeOr.clear();
        this.group.clear();
        this.order.clear();
        this.limit = "";
        this.distinct = null;
        this.params.clear();
    }

    private String prepareInsertQuery() {
        StringBuilder sb = new StringBuilder();

        // sqlInsert = "INSERT INTO " + getTableName() + " (" + fieldNames + " sync_isChanged, sync_isDeleted) VALUES (" + questionMarks + " 0, 0)";
        sb.append("INSERT INTO ");

        sb.append(TextUtils.join(tableNames, ","));
        sb.append(" ");
        if (this.into.size() > 0) {
            sb.append("(");
            sb.append(TextUtils.join(this.into, ","));
            sb.append(")");
        }
        sb.append(" VALUES (");
        sb.append(TextUtils.join(this.values, ","));
        sb.append(")");

        return sb.toString();
    }

    private String prepareUpdateQuery() {
        StringBuilder sb = new StringBuilder();

        // sqlInsert = "UPDATE <TABLE> SET key=val,key=val, ... WHERE ... LIMIT ...";
        sb.append("UPDATE ");

        sb.append(TextUtils.join(tableNames, ","));
        sb.append(" SET ");

        ArrayList<String> setVals = new ArrayList<String>();
        for(String key : this.update.keySet()) {
            setVals.add(key + "=" + this.update.get(key));
        }
        sb.append(TextUtils.join(setVals, ","));

        if (this.where.size() > 0) {
            sb.append(" WHERE ");
            sb.append(TextUtils.join(this.where, " AND "));
            sb.append(" ");
        }

        if (!this.limit.equals("")) {
            sb.append(" LIMIT ");
            sb.append(this.limit);
        }

        return sb.toString();
    }

    private String get(QueryType qt) {
        if (qt.equals(QueryType.INSERT))
            return prepareInsertQuery();

        if (qt.equals(QueryType.UPDATE))
            return prepareUpdateQuery();

        StringBuilder sb = new StringBuilder();

        sb.append(qt.toString());

        sb.append(" ");
        if (distinct != null && distinct)
            sb.append(" DISTINCT ");

        if (select.size() > 0)
            sb.append(TextUtils.join(select, ","));
        else
            sb.append("*");

        sb.append(" FROM ");
        sb.append(TextUtils.join(tableNames, ","));
        sb.append(" ");

        if (this.join.size() > 0) {
            sb.append(TextUtils.join(this.join, " "));
            sb.append(" ");
        }

        if (this.where.size() > 0) {
            sb.append(" WHERE ");
            sb.append(TextUtils.join(this.where, " AND "));
            sb.append(" ");
        }

        if (this.like.size() > 0) {
            if (this.where.size() == 0)
                sb.append(" WHERE ");
            else
                sb.append(" AND ");
            sb.append(TextUtils.join(this.like, " AND "));
            sb.append(" ");
        }

        if (this.likeOr.size() > 0) {
            if (this.where.size() == 0 && this.like.size() == 0)
                sb.append(" WHERE ");
            if (this.like.size() > 0 || this.where.size() > 0)
                sb.append(" AND ");
            sb.append(TextUtils.join(this.likeOr, " OR "));
            sb.append(" ");
        }

        if (this.group.size() > 0) {
            sb.append(" GROUP BY ");
            sb.append(TextUtils.join(this.group, ","));
            sb.append(" ");
        }

        if (this.order.size() > 0) {
            sb.append(" ORDER BY ");
            sb.append(TextUtils.join(this.order, ","));
            sb.append(" ");
        }

        if (!this.limit.equals("")) {
            sb.append(" LIMIT ");
            sb.append(this.limit);
        }

        return sb.toString();
    }

    public String getSelect(){
        return get(QueryType.SELECT);
    }

    public String getInsert(){
        return get(QueryType.INSERT);
    }

    public String getUpdate(){
        return get(QueryType.UPDATE);
    }

    public String printSelectDebug() {
        String s  = "SQL: " + getSelect();
        s += "\nPARAMS:\n";
        if (getParams() != null)
            for(String p : getParams())
                s += "\t" + p + "\n";
        return s;
    }

    public static enum QueryType {
        SELECT,
        INSERT,
        UPDATE,
        DELETE;
    }

    public static enum JoinType {
        NONE,
        LEFT,
        RIGHT,
        INNER,
        OUTER,
        LEFT_OUTER,
        RIGHT_OUTER;

        public String asString(){
            if (this == NONE)
                return "";
            else
                return this.toString().replace("_", " ");
        }
    }

    private static class TextUtils {

        public static String join(ArrayList<String> array, String separator) {
            return join((String [])array.toArray(new String[array.size()]), separator, 0, array.size());
        }

        public static String join(Object[] array, String separator, int startIndex, int endIndex) {
            if (array == null) {
                return null;
            }
            int noOfItems = endIndex - startIndex;
            if (noOfItems <= 0) {
                return null;
            }

            StringBuilder buf = new StringBuilder(noOfItems * 16);
            buf.append(array[startIndex]);

            if (endIndex - startIndex > 1) {
                for (int i = startIndex + 1; i < endIndex; i++) {
                    buf.append(separator);
                    if (array[i] != null) {
                        buf.append(array[i]);
                    }
                }
            }
            return buf.toString();
        }
    }

    public static int getVersion() {
        return 1;
    }

}
