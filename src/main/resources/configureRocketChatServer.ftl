<html>
<head>
    [@ui.header pageKey="RocketChat Server Global Configuration" title=true /]
    <meta name="decorator" content="adminpage">
</head>
<body>

    [@ui.clear /]

    [@ww.form
        id='configureRocketChatServer'
        action='updateConfigureRocketChatServer.action'
        submitLabelKey='Update Configuration'
        titleKey='RocketChat Server'
        description='Send IM notifications using RocketChat.']
        
        [@ww.textfield name='rcServer' labelKey="rc.server.global" required='true' value =rcServer descriptionKey='rc.server.global.description' /]
        [@ww.textfield name='rcUser' labelKey="rc.user.global" required='true' value =rcUser descriptionKey='rc.user.description' /]
        [@ww.password name='rcPassword' labelKey="rc.password.global" required='true' value=rcPassword showPassword="true" descriptionKey='rc.password.description' /]
        
        [@ui.clear /]

    [/@ww.form]


</body>
</html>