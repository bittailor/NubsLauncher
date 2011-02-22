require 'fileutils'

if ARGV.include?("-u")
   puts "Wait for nubs to shut down ..." 
   sleep(5) 
end

pwd = Dir.pwd
server_dir =  File.dirname(__FILE__)
Dir.chdir(server_dir)

version = `java -jar nubs.exe --version`.strip
puts "Install Version  #{version}" 

rake_bat = `utility\\which.cmd rake.bat`.strip.gsub("\\","/")
install_dir = File.dirname(rake_bat).gsub("/","\\")

old_nubs = `utility\\which.cmd nubs.exe`.strip

if !old_nubs.nil? && !old_nubs.empty? && File.dirname(old_nubs) != install_dir && File.dirname(old_nubs) != server_dir.gsub("/","\\")   
   puts "delete old nubs from #{old_nubs}" 
   system("del #{old_nubs}")
end
puts "install nubs version #{version} to #{install_dir}"
FileUtils.cp("nubs.exe",install_dir)

Dir.chdir(pwd)

if ARGV.include?("-u")
   exec("nubs.exe")
else
   system("pause")
end

