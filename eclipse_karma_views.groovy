eclipse.views.create("Karma Runner")
	     .clear()
	     .add.browser()
	     .open("hhttp://localhost:9876/node_modules/karma/bin/karma)

eclipse.views.create("Website")
	     .clear()
   	     .add.browser()
   	     .open("http://localhost:9879/index.html")

def view      = eclipse.views.create("Karma Runner - debug ").clear()
																					            .set.layout.grid();
def runButton = view.add.toolBar()
	            .add_Button("run", images.get("IMG_TOOL_REDO_HOVER"));

def browser   = view.add.panel()
		     .set.layout.grid_Grab().add.browser();

def openKarmaDebug = new Runnable() { public void run() 
	{
		Thread.start { 
				browser.open("http://localhost:9879/node_modules/karma/bin/karma/debug.html");	
				view.refresh();
			     };
	}};

runButton.onClick(openKarmaDebug);

view.refresh();

openKarmaDebug.run();