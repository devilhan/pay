<html>
<head>
    <title>支付宝支付测试</title>
</head>
<body>
    ${body}
<script>
    $(function(){
        //定时器，不停地请求后台的api
        setInterval(function () {
            console.log('开始查询支付状态');
            $.ajax({
                url: '/pay/queryByOrderId',
                data:{
                    'orderId': $("#orderId").text()
                },
                success: function(result){
                    if ( result.platformStatus != null && result.platformStatus === 'SUCCESS') {
                        location.href = $("#returnUrl").text()
                    }
                    console.log(result)
                },
                error: function(result){
                    console.log(result);
                }
            })
        },2000)
    });
</script>
</body>
</html>