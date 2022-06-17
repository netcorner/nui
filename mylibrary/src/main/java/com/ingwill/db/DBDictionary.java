package com.ingwill.db;

public class DBDictionary {

    private static DBStructure structure ;

    public static void clear()
    {
        structure=null;
    }
    public static DBStructure getRelationDictionary(BaseSQLMap map)
    {
        if (structure==null)
        {
            structure=new SqliteStructure(map);
        }
        return structure;
    }

}
