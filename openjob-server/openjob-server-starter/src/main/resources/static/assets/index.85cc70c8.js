import{s,J as e}from"./request.c46845dc.js";function i(){return{getListTaskList:t=>s({url:e.listTask,method:"get",params:t}),getListChildList:t=>s({url:e.listChild,method:"get",params:t}),stop:t=>s({url:e.stopTask,method:"post",data:t}),getTaskLog:t=>s({url:e.listTaskLog,method:"get",params:t})}}export{i as u};