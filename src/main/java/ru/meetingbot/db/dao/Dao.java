package ru.meetingbot.db.dao;

import ru.meetingbot.db.DBConst;
import ru.meetingbot.db.connection.ConnectionFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Dao<T> implements DBConst {

    private static final Logger logger = Logger.getLogger(Dao.class.getName());

    public abstract Optional<T> get(T t);

    public abstract List<T> getAll();

    public abstract int create(T t);

    public abstract int update(T t);

    public abstract int delete(T t);

    /**
     * Установить в PreparedStatement значения разного типа.
     *  if (obj.getClass == Integer.class) {
     *      prStmt.setInt(index, (int) obj);
     *  }
     */
    private PreparedStatement setObjInPrStmt(PreparedStatement prStmt, int index, Object obj) throws SQLException {
        if (obj == null) {
            prStmt.setObject(index, null);
            return prStmt;
        }

        Class<?> objClass = obj.getClass();
        if (objClass == Integer.class) {
            prStmt.setInt(index, (int) obj);

        } else if (objClass == Short.class) {
            prStmt.setShort(index, (short) obj);

        } else if (objClass == String.class) {
            prStmt.setString(index, (String) obj);

        } else if (objClass == Long.class) {
            prStmt.setLong(index, (Long) obj);

        } else if (objClass == Boolean.class) {
            prStmt.setBoolean(index, (boolean) obj);

        } else if (objClass == LocalDate.class) {
            prStmt.setDate(index, Date.valueOf((LocalDate) obj));

        } else if (objClass == Date.class) {
            prStmt.setDate(index, (Date) obj);

        } else {
            RuntimeException exception = new RuntimeException("Не обрабатываются объекты класса " + objClass.getName());
            logger.log(Level.WARNING, "", exception);
            throw exception;
        }

        return prStmt;
    }


    /**
     * Объектов должно быть чётное число. Массив идёт парами:
     *  String название_столбца, Object значение,
     *  String название_столбца, Object значение, ...
     */
    protected ResultSet executeQuery(String sql, Object... objects) {
        try (Connection connection = ConnectionFactory.getConnection()) {

            PreparedStatement prStmt = connection.prepareStatement(sql);

            int index = 1;
            for (int i = 1; i < objects.length; i += 2) {
                setObjInPrStmt(prStmt, index, objects[i]);
                index++;
            }

            logger.info(prStmt.toString());
            return prStmt.executeQuery();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQL", e);
            throw new RuntimeException(e);
        }
    }
    protected ResultSet executeQuery(Connection connection, String sql, Object... objects) throws SQLException {
        PreparedStatement prStmt = connection.prepareStatement(sql);

        int index = 1;
        for (int i = 1; i < objects.length; i += 2) {
            setObjInPrStmt(prStmt, index, objects[i]);
            index++;
        }

        logger.info(prStmt.toString());
        return prStmt.executeQuery();
    }

    protected ResultSet getAnd(String table, Object... objects) {
        return getAnd(null, table, objects);
    }
    protected ResultSet getAnd(Connection connection, String table, Object... objects) {
        return get(connection, true, table, objects);
    }
    protected ResultSet getOr(String table, Object... objects) {
        return getOr(null, table, objects);
    }
    protected ResultSet getOr(Connection connection, String table, Object... objects) {
        return get(connection, false, table, objects);
    }

    protected ResultSet get(Connection connection, boolean and, String table, Object... objects) {
        if (objects.length < 2) {
            RuntimeException exception = new RuntimeException("Не хватает параметров в get()");
            logger.log(Level.WARNING, "", exception);
            throw exception;
        }

        StringBuilder builder = new StringBuilder()
                .append("SELECT * FROM ").append(table)
                .append(" WHERE ").append((String) objects[0]).append("=?");

        if (objects.length > 2) {
            for (int i = 2; i < objects.length; i+=2) {
                if (and) {
                    builder.append(" AND ");
                } else {
                    builder.append(" OR ");
                }
                builder.append((String) objects[i]).append("=?");
            }
        }

        String sql = builder.toString();

        if(connection == null) {
            return executeQuery(sql, objects);
        } else {
            try {
                return executeQuery(connection, sql, objects);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "SQL", e);
                throw new RuntimeException(e);
            }
        }
    }

    protected ResultSet getAll(String table) {
        return getAll(null, table);
    }
    protected ResultSet getAll(Connection connection, String table) {
        String sql = new StringBuilder()
                .append("SELECT * FROM ").append(table).toString();

        if (connection == null) {
            return executeQuery(sql);
        } else {
            try {
                return executeQuery(connection, sql);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "SQL", e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Объектов должно быть чётное число. Массив идёт парами:
     *  String название_столбца, Object значение,
     *  String название_столбца, Object значение, ...
     */
    protected int executeUpdate(String sql, Object... objects) {
        try(Connection connection = ConnectionFactory.getConnection()) {
            PreparedStatement prStmt = connection.prepareStatement(sql);

            int index = 1;
            for (int i = 1; i < objects.length; i+=2) {
                setObjInPrStmt(prStmt, index, objects[i]);
                index++;
            }

            logger.info(prStmt.toString());
            return prStmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "SQL: " + e.getSQLState(), e);
            return 0;
        }
    }
    protected int executeUpdate(Connection connection, String sql, Object... objects) throws SQLException {
        PreparedStatement prStmt = connection.prepareStatement(sql);

        int index = 1;
        for (int i = 1; i < objects.length; i+=2) {
            setObjInPrStmt(prStmt, index, objects[i]);
            index++;
        }

        logger.info(prStmt.toString());
        return prStmt.executeUpdate();
    }

    protected int create(String table, Object... objects) {
        return create(null, table, objects);
    }
    protected int create(Connection connection, String table, Object... objects) {
        if (objects.length < 2) {
            RuntimeException exception = new RuntimeException("Не хватает параметров в create()");
            logger.log(Level.WARNING, "", exception);
            throw exception;
        }

        StringBuilder builder = new StringBuilder()
                .append("INSERT INTO ")
                .append(table)
                .append(" (")
                .append((String) objects[0]);

        if (objects.length > 2) {
            for (int i = 2; i < objects.length; i+=2) {
                builder.append(", ")
                        .append((String) objects[i]);
            }
        }

        builder.append(") VALUES ( ?");

        for (int i = 1; i < objects.length / 2; i++) {
            builder.append(", ?");
        }

        builder.append(")");

        String sql = builder.toString();

        if (connection == null) {
            return executeUpdate(sql, objects);
        } else {
            try {
                return executeUpdate(connection, sql, objects);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "SQL", e);
                return 0;
            }
        }
    }

    protected int updateByKey(String table, Object... objects) {
        return updateByKey(null, table, objects);
    }
    protected int updateByKey(Connection connection, String table, Object... objects) {
        if (objects.length < 4) {
            RuntimeException exception = new RuntimeException("Не хватает параметров в updateByKey()");
            logger.log(Level.WARNING, "", exception);
            throw exception;
        }

        StringBuilder builder = new StringBuilder()
                .append("UPDATE ")
                .append(table)
                .append(" SET ")
                .append((String) objects[0])
                .append("=?");

        if (objects.length > 4) {
            for (int i = 2; i < objects.length-2; i+=2) {
                builder.append(", ")
                        .append((String) objects[i])
                        .append("=?");
            }
        }

        builder.append(" WHERE ")
                .append((String) objects[objects.length-2])
                .append("=?");

        String sql = builder.toString();

        if (connection == null) {
            return executeUpdate(sql, objects);
        } else {
            try {
                return executeUpdate(connection, sql, objects);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "SQL", e);
                return 0;
            }
        }
    }

    /**
     * Обновить строку, где первые fields пар в массиве objects[] это те поля,
     * которые надо обновить. А следующие пары, это те поля, по которым надо искать.
     */
    protected int updateWhere(String table, int fields, Object... objects) {
        return updateWhere(null, table, fields, objects);
    }
    protected int updateWhere(Connection connection, String table, int fields, Object... objects) {
        if (objects.length < 4) {
            RuntimeException exception = new RuntimeException("Не хватает параметров в updateByKey()");
            logger.log(Level.WARNING, "", exception);
            throw exception;
        }

        StringBuilder builder = new StringBuilder()
                .append("UPDATE ")
                .append(table)
                .append(" SET ")
                .append((String) objects[0])
                .append("=?");

        //f, #, f, #, f, #, k, $, k, $
        //0, 1, 2, 3, 4, 5, 6, 7, 8, 9
        if (objects.length > 4) {
            for (int i = 2; i < fields * 2; i+=2) {
                builder.append(", ")
                        .append((String) objects[i])
                        .append("=?");
            }
        }

        builder.append(" WHERE ")
                .append((String) objects[fields * 2])
                .append("=?");

        for (int i = fields * 2 + 2; i < objects.length; i+=2) {
            builder.append(" AND ")
                    .append((String) objects[i])
                    .append("=?");
        }

        String sql = builder.toString();

        if (connection == null) {
            return executeUpdate(sql, objects);
        } else {
            try {
                return executeUpdate(connection, sql, objects);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "SQL", e);
                return 0;
            }
        }
    }


    /**
     * Удаляет строку по значению столбца.
     * ----------------------------------------------------------------
     * Если будет несколько столбцов (DELETE FROM table WHERE ... AND ... AND ...),
     * то не удаляет строку за один раз!
     *
     * Например таблица table:
     * столбцы: (id,  a,  b)
     * строка:  (11, 22, 33)
     *
     * после первого раза
     *      DELETE FROM table WHERE id=11 AND a=22;
     *
     * строка - (11, 0, 0)
     *
     * нужно второй раз вызвать
     *      DELETE FROM table WHERE id=11 AND a=0;
     */
    protected int deleteByKey(String table, String nameColumn, Object obj) {
        return deleteByKey(null, table, nameColumn, obj);
    }
    protected int deleteByKey(Connection connection, String table, String nameColumn, Object obj) {
        String sql = new StringBuilder()
                .append("DELETE FROM ").append(table)
                .append(" WHERE ").append(nameColumn).append("=?")
                .toString();


        if (connection == null) {
            return executeUpdate(sql, nameColumn, obj);
        } else {
            try {
                return executeUpdate(connection, sql, nameColumn, obj);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "SQL", e);
                return 0;
            }
        }
    }

    protected int deleteAnd(String table, Object... objects) {
        return delete(true, table, objects);
    }
    protected int deleteOr(String table, Object... objects) {
        return delete(false, table, objects);
    }
    protected int delete(boolean and, String table, Object... objects) {
        if (objects.length < 2) {
            RuntimeException exception = new RuntimeException("Не хватает параметров в get()");
            logger.log(Level.WARNING, "", exception);
            throw exception;
        }

        StringBuilder builder = new StringBuilder()
                .append("DELETE FROM ").append(table)
                .append(" WHERE ").append((String) objects[0]).append("=?");

        if (objects.length > 2) {
            for (int i = 2; i < objects.length; i+=2) {
                if (and) {
                    builder.append(" AND ");
                } else {
                    builder.append(" OR ");
                }
                builder.append((String) objects[i]).append("=?");
            }
        }

        String sql = builder.toString();

        return executeUpdate(sql, objects);
    }

}
