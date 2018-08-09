// created by sunsong  2018/6/19
$('html,body').height(document.body.clientHeight);


$('.p11').click(function(){
    $('.close').show();
    $('.saw').fadeIn(800);
});

$('.close').click(function(){
    $('.saw').fadeOut(800);
    $('.close').hide();
});

$('.start').click(function(){
    $('.p1').hide();
    $('.p2').show();
    $.ajax({
        type: 'post',
        url: "http://115.233.208.56/worldcup/UserServlet/checkTeam",
        dataType: 'json',
        data: " ",
        success: function(data){
            console.log(data);
            var len = data.length;
            var html="";
            var h = 1;
            for(var i=0;i<len;i++){
                if(data[i].value==1){
                    var s = i+1;
                    if(h==1){
                        html += "<li><img src='images/"+s+".png' alt='' class='limg' data-id='"+data[i].id+"'></li>";
                        h++;
                    }else if(h==2){
                        var s = i+1;
                        html += "<li><img src='images/"+s+".png' alt='' class='limg' data-id='"+data[i].id+"'></li><div class='both'></div>";
                        h=1;
                    }
                }

            }

            $('.imgul').append(html);

        },
    });

});







var choice ='';//用户选项


$(document).on("click", ".limg",function(){


    // 球队
    function team(n){
        var team = '';
        var n = n;
        switch(n){
            case 1:
                team = '乌拉圭';
                break;
            case 2:
                team = '葡萄牙';
                break;
            case 3:
                team = '法国';
                break;
            case 4:
                team = '阿根廷';
                break;
            case 5:
                team = '巴西';
                break;
            case 6:
                team = '墨西哥';
                break;
            case 7:
                team = '比利时';
                break;
            case 8:
                team = '日本';
                break;
            case 9:
                team = '西班牙';
                break;
            case 10:
                team = '俄罗斯';
                break;
            case 11:
                team = '克罗地亚';
                break;
            case 12:
                team = '丹麦';
                break;
            case 13:
                team = '瑞典';
                break;
            case 14:
                team = '瑞士';
                break;
            case 15:
                team = '哥伦比亚';
                break;
            case 16:
                team = '英格兰';
                break;

        }
        return team;
    }



    // 主播
    function anchor(n){
        var anchor = '';
        var n = n;
        switch(n){
            case 1:
                anchor = '逸婷';
                break;
            case 2:
                anchor = '冰冰';
                break;
            case 3:
                anchor = '金琦';
                break;
            case 4:
                anchor = '小歪';
                break;
            case 5:
                anchor = '夏天';
                break;
            case 6:
                anchor = '周飞';
                break;
            case 7:
                anchor = '铭伟';
                break;
            case 8:
                anchor = '张博';
                break;
            case 9:
                anchor = '杨光';
                break;
            case 10:
                anchor = '许一';
                break;
            case 11:
                anchor = '陈芒';
                break;
            case 12:
                anchor = '乐乐';
                break;
            case 13:
                anchor = '小雨';
                break;
            case 14:
                anchor = '奥囡囡';
                break;
            case 15:
                anchor = '游游';
                break;
            case 16:
                anchor = '小巍';
                break;
        }
        return anchor;
    }



    $('.p3').show();
    choice = $(this).data('id');
    console.log(choice);
    var url = $(this).attr('src');
    $('.p3_t').attr('src',url);
    $('.p3_t6').attr('src',url);
    var team = team(choice);
    var anchor = anchor(choice);
    console.log(team);
    $('.team').text(team);
    $('.anchor').text(anchor);
});


$('.back').click(function(){
    $('.p3').hide();
});

$('.p3_s').click(function(){

    $.ajax({
        type: 'post',
        url: "http://115.233.208.56/worldcup/UserServlet/checkActive",
        dataType: 'json',
        data: " ",
        success: function(data){
            console.log(data);
            if(data.active=="true"){
                findUser(wx_id);  //查询用户信息
            }else{
                alert('竞猜暂未开启！');
            }
        },
    });


});

$('.ok').click(function(){
    updateUser(wx_id);   //初次填写信息
});

var wx_id = GetQueryString("openid"); //用户openid



function findUser(wx_id){  //查询用户信息
    $.ajax({
        type: 'post',
        url: "http://115.233.208.56/worldcup/UserServlet/findUser",
        data: {
            wechat_id: wx_id,
        },
        dataType: 'json',
        success: function(data){
            console.log(data.userChoice);
            var  userChoice = data.userChoice;
            if(userChoice==0){
                $('.p2').hide();
                $('.p3').hide();
                $('.p4').show();
                $('html,body').height(document.body.clientHeight);
            }else{
                updateChoice(wx_id);  //更新站队
            }

        },

    });
}

function updateUser(wx_id){  //更新用户信息 详细
    var user = $('.user').val();
    var number = $('.number').val();
    var address = $('.address').val();
    var data = JSON.stringify({wechat_id:wx_id,user_name:user,user_tel:number,user_addr:address,user_choice:choice});
    $.ajax({
        type: 'post',
        url: "http://115.233.208.56/worldcup/UserServlet/updateUser",
        data: data,
        dataType: 'json',
        success: function(data){
            console.log(data);  //更新成功
            // $('.p4').hide();
            $('.p6').show();

            html2canvas(document.querySelector("#pps")).then(canvas => {
                var image = new Image();
            image.crossOrigin = '*';
            image.id = "canImg";
            image.crossOrigin = "Anonymous"
            image.src = canvas.toDataURL("image/png");

            document.querySelector("#ppp").appendChild(image);});

            document.body.addEventListener('touchmove', function (event){
                event.preventDefault(); //阻止规定事件的默认动作
            }, false);

},
});
}

function updateChoice(wx_id){  //更新站队选择
    var data = JSON.stringify({wechat_id:wx_id,choice:choice});
    $.ajax({
        type: 'post',
        url: "http://115.233.208.56/worldcup/UserServlet/updateChoice",
        data: data,
        dataType: 'json',
        success: function(data){
            console.log(data); //站队更新成功
            // $('.p2').hide();
            if(data.chooseLog=="重新站队成功"){
                $('.p3').hide();
                $('.p6').show();
                alert(data.chooseLog);

                html2canvas(document.querySelector("#pps")).then(canvas => {
                    var image = new Image();
                image.crossOrigin = '*';
                image.id = "canImg";
                image.crossOrigin = "Anonymous"
                image.src = canvas.toDataURL("image/png");

                document.querySelector("#ppp").appendChild(image);});

            document.body.addEventListener('touchmove', function (event){
                event.preventDefault(); //阻止规定事件的默认动作
            }, false);




        }else{
            alert(data.chooseLog);
}

},
});
}












function GetQueryString(name)
    {
        var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if(r!=null)return  unescape(r[2]); return null;
    }
    $(function() {
        if (GetQueryString("redirect") == "true") {
            $.ajax({
                type: "get",
                url: "http://laxback.duapp.com/worldcup/testtest/shareBack?",
                dataType: "text",
                cache: false,
                data: {"nowurl": encodeURIComponent(window.location.href.split('#')[0])},

                success: function (data) {
                    // $(".result").html("hello login success");
                    var openid=GetQueryString("openid");
                  //  alert("hello login success "+openid);
                    var json = $.parseJSON(data);

                    wx.config({
                        debug: false, // ¿ªÆôµ÷ÊÔÄ£Ê½,µ÷ÓÃµÄËùÓÐapiµÄ·µ»ØÖµ»áÔÚ¿Í»§¶Ëalert³öÀ´£¬ÈôÒª²é¿´´«ÈëµÄ²ÎÊý£¬¿ÉÒÔÔÚpc¶Ë´ò¿ª£¬²ÎÊýÐÅÏ¢»áÍ¨¹ýlog´ò³ö£¬½öÔÚpc¶ËÊ±²Å»á´òÓ¡¡£
                        appId: json.appId, // ±ØÌî£¬¹«ÖÚºÅµÄÎ¨Ò»±êÊ¶
                        timestamp: json.timestamp, // ±ØÌî£¬Éú³ÉÇ©ÃûµÄÊ±¼ä´Á
                        nonceStr: json.nonceStr, // ±ØÌî£¬Éú³ÉÇ©ÃûµÄËæ»ú´®
                        signature: json.signature,// ±ØÌî£¬Ç©Ãû£¬¼û¸½Â¼1
                        jsApiList: ['onMenuShareAppMessage', 'onMenuShareTimeline', 'onMenuShareQQ', 'onMenuShareWeibo', 'onMenuShareQZone'] // ±ØÌî£¬ÐèÒªÊ¹ÓÃµÄJS½Ó¿ÚÁÐ±í£¬ËùÓÐJS½Ó¿ÚÁÐ±í¼û¸½Â¼2
                    });
                    wx.ready(function () {
                        // 1 ÅÐ¶Ïµ±Ç°°æ±¾ÊÇ·ñÖ§³ÖÖ¸¶¨ JS ½Ó¿Ú£¬Ö§³ÖÅúÁ¿ÅÐ¶Ï

                        wx.checkJsApi({
                            jsApiList: [
                                "onMenuShareTimeline",//·ÖÏíµ½ÅóÓÑÈ¦,
                                "onMenuShareAppMessage"//·ÖÏí¸øÅóÓÑ
                            ],
                            success: function (res) {
                               // alert(JSON.stringify(res));
                            }
                        });

                        //ÏÂÃæ¾Í¿ÉÒÔÐ´Ò»ÏµÁÐµÄ½Ó¿ÚÁË
                        //ÅóÓÑÈ¦
                        wx.onMenuShareTimeline({
                            title:'女主播带你站队世界杯', // ·ÖÏí±êÌâ
                            desc: '女主播带你站队世界杯',
                            link: json.shareurl, // ·ÖÏíÁ´½Ó£¬¸ÃÁ´½ÓÓòÃû»òÂ·¾¶±ØÐëÓëµ±Ç°Ò³Ãæ¶ÔÓ¦µÄ¹«ÖÚºÅJS°²È«ÓòÃûÒ»ÖÂ
                            imgUrl: json.logoimgurl, // ·ÖÏíÍ¼±ê
                            success: function () {
                                // ÓÃ»§µã»÷ÁË·ÖÏíºóÖ´ÐÐµÄ»Øµ÷º¯Êý
                                console.log("shareurl"+json.shareurl);
                            }
                        });
                        //ÅóÓÑ
                        wx.onMenuShareAppMessage({
                            title: '女主播带你站队世界杯', // ·ÖÏí±êÌâ
                            desc: '女主播带你站队世界杯',
                            link: json.shareurl, // ·ÖÏíÁ´½Ó£¬¸ÃÁ´½ÓÓòÃû»òÂ·¾¶±ØÐëÓëµ±Ç°Ò³Ãæ¶ÔÓ¦µÄ¹«ÖÚºÅJS°²È«ÓòÃûÒ»ÖÂ
                            imgUrl: json.logoimgurl, // ·ÖÏíÍ¼±ê
                            type: '', // ·ÖÏíÀàÐÍ,music¡¢video»òlink£¬²»ÌîÄ¬ÈÏÎªlink
                            dataUrl: '', // Èç¹ûtypeÊÇmusic»òvideo£¬ÔòÒªÌá¹©Êý¾ÝÁ´½Ó£¬Ä¬ÈÏÎª¿Õ
                            success: function () {
                                // ÓÃ»§µã»÷ÁË·ÖÏíºóÖ´ÐÐµÄ»Øµ÷º¯Êý
                            }
                        });

                    });


                    wx.error(function (res) {
                        alert(res.errMsg);
                    });
                }

            });
        }else{
            $.ajax({
                type:"GET",
                url:"http://laxback.duapp.com/worldcup/testtest/wxLogin?",
                dataType:"jsonp",
                cache:false,
                data:{},
                complete : function(XMLHttpRequest, textStatus) {
                    // Í¨¹ýXMLHttpRequestÈ¡µÃÏìÓ¦Í·£¬REDIRECT
                    var redirect = XMLHttpRequest.getResponseHeader("REDIRECT");//ÈôHEADERÖÐº¬ÓÐREDIRECTËµÃ÷ºó¶ËÏëÖØ¶¨Ïò
                    //alert(redirect);
                    if (redirect == "REDIRECT") {
                        var win = window;
                        while (win != win.top){
                            win = win.top;
                        }
                        console.log(XMLHttpRequest.getResponseHeader("CONTEXTPATH"));
                        win.location.href= XMLHttpRequest.getResponseHeader("CONTEXTPATH");

                    }
                },
                success:function(data){




                }

            });
        }
    });