<html>
<head>
    <meta charset="utf-8">
    <title>微信支付测试</title>
</head>
<body>
<div id="myQrCode"></div>
<div  id="orderId">${orderId}</div>
<div  id="returnUrl">${returnUrl}</div>
<script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.5.1/jquery.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/jquery.qrcode/1.0/jquery.qrcode.min.js"></script>
<script>

    $("#myQrCode").qrcode({
        text : "${codeUrl}"
    })

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