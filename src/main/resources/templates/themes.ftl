	////////////////////////////////////////////////////////////
    //theme 部分
    function selectChange(value){
        var theme = value;
        myChart.showLoading();
        //$(themeSelector).val(theme);
        if (theme != 'default') {
            window.location.hash = value;
            require(['themePath/' + theme], function(tarTheme){
                curTheme = tarTheme;
                setTimeout(refreshTheme, 500);
            })
        }
        else {
            window.location.hash = '';
            curTheme = {};
            setTimeout(refreshTheme, 500);
        }
    }
    function refreshTheme(){
        myChart.hideLoading();
        myChart.setTheme(curTheme);
    }
    
	
    selectChange("${theme}");
    ///
    ///theme 部分
    ////////////////////////////////////////////////////////////////////////////////////