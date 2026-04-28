package util;

public class Config {
    public static final String SUPABASE_URL = "https://sjqbrvdjafdhxdmvysyv.supabase.co";
    public static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNqcWJydmRqYWZkaHhkbXZ5c3l2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzczMzMzMzEsImV4cCI6MjA5MjkwOTMzMX0.bdhwCAJHTNJaZtMF79vxe3CoQ-iuRIezi6hOK5HR7pg";
    public static final String STORAGE_BUCKET = "submissions";
    public static final String STORAGE_PUBLIC_URL = SUPABASE_URL + "/storage/v1/object/public/" + STORAGE_BUCKET + "/";
}