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
puts "old_nubs @ #{old_nubs}" 

#if !old_nubs.nil? && !old_nubs.empty? && File.dirname(old_nubs).downcase != install_dir.downcase && File.dirname(old_nubs).downcase != server_dir.gsub("/","\\").downcase   
#   if !(old_nubs.downcase=="s:\Gruppe\TE\Verwaltung\Tools\NUBSLauncher\nubs.exe".downcase && old_nubs.downcase=="nubs.exe".downcase ) 
#      puts "delete old nubs from #{old_nubs}" 
#      system("del #{old_nubs}")
#   end
#end

puts "install nubs version #{version} to #{install_dir}"
FileUtils.cp("nubs.exe",install_dir)

Dir.chdir(pwd)

if ARGV.include?("-u")
   exec("nubs.exe")
else
   system("pause")
end

