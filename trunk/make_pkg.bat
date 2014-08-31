call ant clean
ping -n 3 127.0.0.1>nul
call ant make_channels
pause>nul