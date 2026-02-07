import J2NDivider from "@repo/components/atoms/J2NDivider";
import J2NFooter from "@repo/components/molecules/J2NFooter";
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
      />
      <main className="bg-j2n-sand-100 min-h-screen">{children}</main>
    </>
  );
}
