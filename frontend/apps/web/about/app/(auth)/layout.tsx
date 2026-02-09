import J2NHeader from "@repo/components/molecules/J2NHeader";

export default function MainLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <>
      <J2NHeader
        title="Jadon Nguyen"
        className="bg-transparent!"
        redirectUrl="/login"
        hideMenu
        hideLogo
      />
      <main className="bg-j2n-sand-100 min-h-screen">{children}</main>
    </>
  );
}
