import { createClient } from '@supabase/supabase-js';

const supabaseUrl = 'https://istyppesbrfamkmvrgcv.supabase.co';
const supabaseAnonKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlzdHlwcGVzYnJmYW1rbXZyZ2N2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDcyNTU5OTcsImV4cCI6MjA2MjgzMTk5N30.EQaIIGb-ZM7mbqy-8SDBtrXFpgXDXyL81FwOpn9PEk8';

export const supabase = createClient(supabaseUrl, supabaseAnonKey);