package com.neher.ecl.share;

public class Env {

    public static final class remote{
        /*public static final String register_url = "http://192.168.0.196/123ShareWeb/public/index.php/api/register";
        public static final String login_url = "http://192.168.0.196/123ShareWeb/public/index.php/api/login";
        public static final String change_status_url = "http://192.168.0.196/123ShareWeb/public/index.php/api/change-status";
        public static final String sharing_request_url = "http://192.168.0.196/123ShareWeb/public/index.php/api/sharing-request";
*/

        public static final String register_url = "http://139.162.60.218/test/public/index.php/api/register";
        public static final String login_url = "http://139.162.60.218/test/public/index.php/api/login";
        public static final String change_status_url = "http://139.162.60.218/test/public/index.php/api/change-status";
        public static final String sharing_request_url = "http://139.162.60.218/test/public/index.php/api/sharing-request";
    }

    public static final class sp{
        public static final String sp_name = "com.neher.ecl.share.user.info";
        public static final String access_token = "access_token";
        public static final String access_token_yes = "yes";
        public static final String user_name = "user_name";
        public static final String user_mobile = "user_mobile";
        public static final String user_gender = "user_gender";
        public static final String user_age = "user_age";
        public static final String user_password = "user_password";
        public static final String user_share_with = "user_share_with";
    }

    public static final class user{
        public static final String male = "M";
        public static final String female = "F";
        public static final String both = "B";
    }

    public static final class request{
        public static final String alive = "1";
        public static final String cancle = "2";
        public static final String time_out = "3";
        public static final String success = "4";
    }
}
