package com.wsc.qa.constants;

import com.alibaba.fastjson.JSONObject;

public class CommonConstants {

    /**
     * @Description 错误代码
     */
    public static enum ErrorCode {
        ERROR_ACCOUTWRONG(0x00001, "账户密码不正确"),
        ERROR_REPEAT_REQUEST(0x00003, "重复申请"),
        ERROR_REQUEST_TIMEOUT(0x00005, "请求过期"),
        ERROR_IP_BLOCKED(0xFF000, "您的 ip 被限制访问"),
        ERROR_ILLEGAL_PARAMTER(0xFF001, "缺少参数或参数不合法")
        ;


        private Integer code;
        private String description;
        private String tempDesc;

        public String getTempDesc() {
            return tempDesc;
        }

        public void setTempDesc(String tempDesc) {
            this.tempDesc = tempDesc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        ErrorCode(Integer code, String description) {
            this.code = code;
            this.description = description;
        }



        public String toString() {
            return toString(null);
        }

        /**
         * 用于返回注解描述信息
         *
         * @param customerDescription
         * @return
         */
        public String toString(String customerDescription) {
            JSONObject json = new JSONObject();
            JSONObject error = new JSONObject();
            error.put("code", code);
            String userDefinedDescrption = description;
            if (customerDescription != null && customerDescription.length() > 0) {
                userDefinedDescrption = userDefinedDescrption + ";" + customerDescription;
            }
            error.put("description", userDefinedDescrption);
            json.put("error", error);

            return json.toString();
        }
    }
    
}